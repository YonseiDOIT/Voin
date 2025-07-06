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
        try {
            // accessToken으로 카카오 사용자 정보 조회
            Map<String, Object> userInfo = kakaoAuthService.getUserInfo(request.getAccessToken());
            String kakaoId = userInfo.get("id").toString();
            String kakaoNickname = extractKakaoNickname(userInfo);
            String kakaoProfileImage = extractKakaoProfileImage(userInfo);

            // 이미 가입된 사용자인지 확인
            if (memberRepository.findByKakaoId(kakaoId).isPresent()) {
                throw new IllegalStateException("이미 가입된 사용자입니다.");
            }

            // 닉네임 설정
            String finalNickname = request.getUseKakaoNickname() ? 
                    kakaoNickname : request.getNickname();
            
            // 커스텀 닉네임 검증 (카카오 닉네임도 중복 검사는 수행)
            String validationError = nicknameValidator.validateNickname(finalNickname);
            if (validationError != null) {
                throw new IllegalArgumentException(validationError);
            }

            return SignupResponse.builder()
                    .currentStep(SignupStep.PROFILE_IMAGE_SETTING)
                    .nextStep(SignupStep.COMPLETED)
                    .currentNickname(finalNickname)
                    .currentProfileImage(kakaoProfileImage)
                    .isCompleted(false)
                    .nextStepUrl("/signup/profile-image")
                    .build();

        } catch (Exception e) {
            log.error("닉네임 설정 처리 실패: {}", e.getMessage());
            throw new RuntimeException("닉네임 설정을 처리할 수 없습니다: " + e.getMessage());
        }
    }

    /**
     * 프로필 이미지 설정 및 회원가입 완료
     */
    public SignupResponse setProfileImageAndComplete(ProfileImageSettingRequest request) {
        try {
            // accessToken으로 카카오 사용자 정보 조회
            Map<String, Object> userInfo = kakaoAuthService.getUserInfo(request.getAccessToken());
            String kakaoId = userInfo.get("id").toString();
            String kakaoNickname = extractKakaoNickname(userInfo);
            String kakaoProfileImage = extractKakaoProfileImage(userInfo);

            // 이미 가입된 사용자인지 확인
            if (memberRepository.findByKakaoId(kakaoId).isPresent()) {
                throw new IllegalStateException("이미 가입된 사용자입니다.");
            }

            // 닉네임 결정 - 이전 단계에서 설정된 닉네임 사용
            String finalNickname = request.getUseKakaoNickname() ? 
                    kakaoNickname : request.getNickname();
            
            // 닉네임이 설정되지 않은 경우 카카오 닉네임 사용 (fallback)
            if (finalNickname == null || finalNickname.trim().isEmpty()) {
                finalNickname = kakaoNickname;
                log.warn("설정된 닉네임이 없어 카카오 닉네임 사용: {}", finalNickname);
            }
            
            log.info("최종 닉네임 결정: {}", finalNickname);

            // 프로필 이미지 설정
            String finalProfileImage;
            
            try {
                if (request.getUseKakaoProfileImage()) {
                    finalProfileImage = kakaoProfileImage;
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
                    .kakaoId(kakaoId)
                    .nickname(finalNickname)
                    .profileImage(finalProfileImage)
                    .friendCode(friendCode)
                    .isActive(true)
                    .build();

            log.info("회원 정보 저장 시작 - 카카오ID: {}, 닉네임: {}, 프로필이미지: {}", 
                    kakaoId, finalNickname, finalProfileImage);

            Member savedMember = memberRepository.save(member);
            
            log.info("회원 정보 저장 완료 - 회원ID: {}, 닉네임: {}, 친구코드: {}", 
                    savedMember.getId(), savedMember.getNickname(), savedMember.getFriendCode());

            // 저장된 회원 정보 검증
            boolean memberExists = memberRepository.existsByKakaoId(kakaoId);
            log.info("DB 저장 검증: 카카오ID {}로 회원 존재 여부 = {}", kakaoId, memberExists);

            // 임시 토큰 생성 (추후 JWT로 교체)
            String tempToken = "VOIN_TOKEN_" + savedMember.getId().toString() + "_" + System.currentTimeMillis();
            log.info("임시 토큰 생성: {}", tempToken);

            return SignupResponse.builder()
                    .currentStep(SignupStep.COMPLETED)
                    .currentNickname(savedMember.getNickname())
                    .currentProfileImage(savedMember.getProfileImage())
                    .isCompleted(true)
                    .jwtToken(tempToken)
                    .build();

        } catch (Exception e) {
            log.error("프로필 이미지 설정 및 회원가입 완료 처리 실패: {}", e.getMessage());
            throw new RuntimeException("회원가입을 완료할 수 없습니다: " + e.getMessage());
        }
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
} 