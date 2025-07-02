package com.voin.service;

import com.voin.constant.SignupStep;
import com.voin.dto.request.NicknameSettingRequest;
import com.voin.dto.request.ProfileImageSettingRequest;
import com.voin.dto.response.SignupResponse;
import com.voin.dto.response.KakaoUserResponse;
import com.voin.entity.Member;
import com.voin.repository.MemberRepository;
import com.voin.util.FriendCodeGenerator;
import com.voin.util.ImageUtil;
import com.voin.util.NicknameValidator;
// import com.voin.util.JwtUtil; // JWT 임시 비활성화
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SignupService {

    private final KakaoAuthService kakaoAuthService;
    private final MemberRepository memberRepository;
    private final FriendCodeGenerator friendCodeGenerator;
    private final ImageUtil imageUtil;
    private final NicknameValidator nicknameValidator;

    // 임시 회원가입 데이터 저장소 (실제로는 Redis나 세션 사용 권장)
    private final Map<String, SignupData> tempSignupData = new HashMap<>();

    /**
     * 카카오 로그인 후 회원가입 프로세스 시작
     */
    public SignupResponse startSignupProcess(String accessToken) {
        try {
            // 카카오 사용자 정보 조회
            Map<String, Object> userInfo = kakaoAuthService.getUserInfo(accessToken);
            
            String kakaoId = userInfo.get("id").toString();
            String kakaoNickname = extractKakaoNickname(userInfo);
            String kakaoProfileImage = extractKakaoProfileImage(userInfo);

            // 이미 가입된 사용자인지 확인
            if (memberRepository.findByKakaoId(kakaoId).isPresent()) {
                throw new IllegalStateException("이미 가입된 사용자입니다.");
            }

            // 카카오 사용자 정보 DTO 생성
            KakaoUserResponse kakaoUserResponse = KakaoUserResponse.builder()
                    .id(Long.valueOf(kakaoId))
                    .nickname(kakaoNickname)
                    .profileImage(kakaoProfileImage)
                    .build();

            // 임시 회원가입 데이터 저장
            SignupData signupData = SignupData.builder()
                    .kakaoId(kakaoId)
                    .kakaoNickname(kakaoNickname)
                    .kakaoProfileImage(kakaoProfileImage)
                    .currentStep(SignupStep.NICKNAME_SETTING)
                    .build();
            
            tempSignupData.put(accessToken, signupData);

            return SignupResponse.builder()
                    .currentStep(SignupStep.NICKNAME_SETTING)
                    .nextStep(SignupStep.PROFILE_IMAGE_SETTING)
                    .kakaoUserInfo(kakaoUserResponse)
                    .currentNickname(kakaoNickname)
                    .isCompleted(false)
                    .nextStepUrl("/signup/nickname")
                    .build();

        } catch (Exception e) {
            log.error("회원가입 프로세스 시작 실패: {}", e.getMessage());
            throw new RuntimeException("회원가입 프로세스를 시작할 수 없습니다: " + e.getMessage());
        }
    }

    /**
     * 닉네임 설정 단계 처리
     */
    public SignupResponse setNickname(NicknameSettingRequest request) {
        SignupData signupData = tempSignupData.get(request.getAccessToken());
        if (signupData == null) {
            throw new IllegalStateException("회원가입 세션이 만료되었습니다. 다시 시도해주세요.");
        }

        // 닉네임 설정
        String finalNickname = request.getUseKakaoNickname() ? 
                signupData.getKakaoNickname() : request.getNickname();
        
        // 커스텀 닉네임 검증 (카카오 닉네임도 중복 검사는 수행)
        String validationError = nicknameValidator.validateNickname(finalNickname);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        
        signupData.setSelectedNickname(finalNickname);
        signupData.setCurrentStep(SignupStep.PROFILE_IMAGE_SETTING);

        return SignupResponse.builder()
                .currentStep(SignupStep.PROFILE_IMAGE_SETTING)
                .nextStep(SignupStep.COMPLETED)
                .currentNickname(finalNickname)
                .currentProfileImage(signupData.getKakaoProfileImage())
                .isCompleted(false)
                .nextStepUrl("/signup/profile-image")
                .build();
    }

    /**
     * 프로필 이미지 설정 및 회원가입 완료
     */
    public SignupResponse setProfileImageAndComplete(ProfileImageSettingRequest request) {
        SignupData signupData = tempSignupData.get(request.getAccessToken());
        if (signupData == null) {
            throw new IllegalStateException("회원가입 세션이 만료되었습니다. 다시 시도해주세요.");
        }

        // 닉네임 검증 - 선택된 닉네임이 없으면 카카오 닉네임 사용
        String finalNickname = signupData.getSelectedNickname();
        if (finalNickname == null || finalNickname.trim().isEmpty()) {
            finalNickname = signupData.getKakaoNickname();
            log.warn("선택된 닉네임이 없어 카카오 닉네임 사용: {}", finalNickname);
        }

        // 프로필 이미지 설정
        String finalProfileImage;
        
        try {
            if (request.getUseKakaoProfileImage()) {
                finalProfileImage = signupData.getKakaoProfileImage();
            } else if (request.getUseFileUpload() && request.getBase64ImageData() != null) {
                // Base64 이미지 업로드 처리
                if (!imageUtil.isValidImageFile(request.getFileName())) {
                    throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (지원 형식: jpg, jpeg, png, gif)");
                }
                finalProfileImage = imageUtil.saveBase64Image(request.getBase64ImageData(), request.getFileName());
            } else {
                finalProfileImage = request.getProfileImageUrl();
            }
        } catch (Exception e) {
            log.error("프로필 이미지 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("프로필 이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 친구 코드 생성
        String friendCode = friendCodeGenerator.generate();
        log.info("친구 코드 생성: {}", friendCode);

        // 회원 정보 저장
        Member member = Member.builder()
                .kakaoId(signupData.getKakaoId())
                .nickname(finalNickname)
                .profileImage(finalProfileImage)
                .friendCode(friendCode)
                .isActive(true)
                .build();

        log.info("회원 정보 저장 시작 - 카카오ID: {}, 닉네임: {}, 프로필이미지: {}", 
                signupData.getKakaoId(), finalNickname, finalProfileImage);

        Member savedMember = memberRepository.save(member);
        
        log.info("회원 정보 저장 완료 - 회원ID: {}, 닉네임: {}, 친구코드: {}", 
                savedMember.getId(), savedMember.getNickname(), savedMember.getFriendCode());

        // 저장된 회원 정보 검증
        boolean memberExists = memberRepository.existsByKakaoId(signupData.getKakaoId());
        log.info("DB 저장 검증: 카카오ID {}로 회원 존재 여부 = {}", signupData.getKakaoId(), memberExists);

        // 임시 토큰 생성 (추후 JWT로 교체)
        String tempToken = "VOIN_TOKEN_" + savedMember.getId().toString() + "_" + System.currentTimeMillis();
        log.info("임시 토큰 생성: {}", tempToken);

        // 임시 데이터 정리
        tempSignupData.remove(request.getAccessToken());

        return SignupResponse.builder()
                .currentStep(SignupStep.COMPLETED)
                .currentNickname(savedMember.getNickname())
                .currentProfileImage(savedMember.getProfileImage())
                .isCompleted(true)
                .jwtToken(tempToken)
                .build();
    }

    /**
     * 카카오 사용자 정보에서 닉네임 추출
     */
    private String extractKakaoNickname(Map<String, Object> userInfo) {
        // KakaoAuthService에서는 이미 properties를 파싱해서 nickname으로 저장
        String nickname = (String) userInfo.get("nickname");
        log.debug("카카오 닉네임 추출: {}", nickname);
        return nickname != null ? nickname : "사용자";
    }

    /**
     * 카카오 사용자 정보에서 프로필 이미지 추출
     */
    private String extractKakaoProfileImage(Map<String, Object> userInfo) {
        // KakaoAuthService에서는 이미 properties를 파싱해서 profile_image로 저장
        String profileImage = (String) userInfo.get("profile_image");
        log.debug("카카오 프로필 이미지 추출: {}", profileImage);
        return profileImage;
    }

    /**
     * 임시 회원가입 데이터 클래스
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class SignupData {
        private String kakaoId;
        private String kakaoNickname;
        private String kakaoProfileImage;
        private String selectedNickname;
        private SignupStep currentStep;
    }
} 