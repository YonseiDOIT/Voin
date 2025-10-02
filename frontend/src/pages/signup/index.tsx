// src/pages/auth/SignUp.tsx
import TopNavigation from '@/components/common/TopNavigation';
import ProfileUploader from '@/components/ProfileUploader';
import TextInput from '@/components/TextInput';
import ActionButton from '@/components/common/ActionButton';

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { authService } from '@/services/authService';
import type { Member } from '@/services/authService';

// 백엔드 응답 래핑 타입 (백엔드 ApiResponse<T>와 맞춤)
type ApiResponse<T> = {
    success: boolean;
    message: string;
    data: T;
};

// 프런트에서 사용하는 최소 Member 구조 (백엔드 MemberResponse와 호환되는 필드만)
type MemberResponse = {
    id: string;
    nickname: string;
    profileImage?: string | null;
    kakaoId: string;
    friendCode: string;
    isActive: boolean;
    createdAt?: string;
    updatedAt?: string;
};

const SignUp = () => {
    const navigate = useNavigate();
    const { hasInitialized, isLoading, isAuthenticated, member, actions } = useAuthStore();

    const [nickname, setNickname] = useState<string>('');
    const [profileImage, setProfileImage] = useState<string | null>(null);
    const [finalProfileImage, setFinalProfileImage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [busy, setBusy] = useState<boolean>(true);

    // 초기 가드 & 프로필 프리필
    useEffect(() => {
        (async () => {
            if (!hasInitialized) {
                await actions.initialize?.();
                return;
            }
            if (isLoading) return;

            // 1) 인증 안 됐으면 로그인으로
            if (!isAuthenticated) {
                navigate('/login', { replace: true });
                return;
            }

            // 2) 신규 아님 플래그 저장돼 있으면 홈으로 (루프 차단)
            const saved = sessionStorage.getItem('isNewMember');
            if (saved === '0') {
                navigate('/home', { replace: true });
                return;
            }

            // 3) 닉네임/이미지 프리필: 스토어 → (없으면) /api/members/me
            let base: MemberResponse | null =
                (member as unknown as MemberResponse | null) ??
                (authService.getStoredUserInfo?.() as unknown as MemberResponse | null) ??
                null;

            if (!base) {
                try {
                    const rawToken = authService.getStoredToken?.();
                    const authHeader =
                        rawToken && rawToken.startsWith('Bearer ') ? rawToken : rawToken ? `Bearer ${rawToken}` : '';

                    const resp = await fetch('/api/members/me', {
                        headers: authHeader ? { Authorization: authHeader } : undefined,
                        credentials: 'include',
                    });

                    if (resp.ok) {
                        // 백엔드: ApiResponse<MemberResponse> 형태
                        const api: ApiResponse<MemberResponse> = await resp.json();
                        base = api.data ?? null;
                    }
                } catch {
                    // 조회 실패 시 프리필 없이 진행
                }
            }

            if (base) {
                setNickname(base.nickname ?? '');
                setProfileImage(base.profileImage ?? null);
            }

            setBusy(false);
        })();
    }, [hasInitialized, isLoading, isAuthenticated, member, navigate, actions]);

    // 프로필 이미지 변경 핸들러
    const handleProfileImageChange = (imageUrl: string | null) => {
        setFinalProfileImage(imageUrl);
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value;
        const sanitizedValue = rawValue.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]/g, '');
        setNickname(sanitizedValue);

        if (rawValue !== sanitizedValue) setError('공백없이 한글, 영문, 숫자만 입력해주세요.');
        else if (sanitizedValue.length > 0 && sanitizedValue.length < 3) setError('3글자 이상 입력해주세요.');
        else setError(null);
    };

    const handleClear = () => {
        setNickname('');
        setError(null);
    };

    const handleButtonClick = async () => {
        try {
            setBusy(true);

            const rawToken = authService.getStoredToken?.();
            if (!rawToken) {
                alert('로그인 정보가 없습니다. 다시 로그인해주세요.');
                navigate('/login', { replace: true });
                return;
            }
            const authHeader = rawToken.startsWith('Bearer ') ? rawToken : `Bearer ${rawToken}`;

            // ✅ 백엔드 스펙에 맞춰 PUT /api/members/me 호출 (닉네임/프로필이미지 동시 업데이트)
            const resp = await fetch('/api/members/me', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: authHeader,
                },
                credentials: 'include',
                body: JSON.stringify({
                    nickname,
                    profileImage: finalProfileImage ?? profileImage,
                }),
            });

            if (!resp.ok) {
                const text = await resp.text();
                throw new Error(`회원정보 업데이트 실패: ${resp.status} ${text}`);
            }

            // 백엔드: ApiResponse<MemberResponse> 형태
            const api: ApiResponse<MemberResponse> = await resp.json();
            const updated = api.data;

            // 스토어 동기화 (프런트 Member 타입으로 매핑)
            const mappedMember: Member = {
                id: updated.id,
                nickname: updated.nickname,
                profileImage: updated.profileImage ?? null,
                kakaoId: updated.kakaoId,
                friendCode: updated.friendCode,
                isActive: updated.isActive,
                createdAt: updated.createdAt ?? '', // (필수라면 빈 문자열이라도 채워야 함)
                updatedAt: updated.updatedAt ?? '',
            };

            actions.setAuthData(rawToken, mappedMember);

            // 신규여부 플래그 제거 후 홈으로
            sessionStorage.removeItem('isNewMember');
            navigate('/home', { replace: true });
        } catch (e) {
            console.error(e);
            alert(e instanceof Error ? e.message : '회원가입 중 오류가 발생했습니다.');
        } finally {
            setBusy(false);
        }
    };

    if (busy) {
        return (
            <div className="flex items-center justify-center py-8">
                <div className="text-gray-500">프로필 정보를 불러오는 중...</div>
            </div>
        );
    }

    return (
        <div className="h-full w-full flex flex-col pt-4">
            <TopNavigation title="만나서 반가워요!" caption="프로필을 완성하고 Voin을 시작해볼까요?" />
            <ProfileUploader defaultImage={profileImage} onChange={handleProfileImageChange} />
            <div className="w-full px-6">
                <TextInput
                    label="닉네임"
                    placeholder="닉네임을 입력해주세요"
                    maxLength={10}
                    onChange={handleChange}
                    onClear={handleClear}
                    value={nickname}
                    error={error}
                />
            </div>
            <div className="mt-auto pb-4 px-6">
                <ActionButton onClick={handleButtonClick} disabled={!nickname || nickname.length < 3 || !!error} buttonText="완료" />
            </div>
        </div>
    );
};

export default SignUp;
