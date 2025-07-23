package com.voin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

/**
 * 회원 정보를 나타내는 엔티티
 */
@Entity
@Table(name = "members", 
       indexes = {
           @Index(name = "idx_member_kakao_id", columnList = "kakao_id"),
           @Index(name = "idx_member_friend_code", columnList = "friend_code")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member extends BaseEntity {

    /**
     * 회원 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    /**
     * 카카오 사용자 고유 ID
     */
    @NotBlank(message = "카카오 ID는 필수입니다")
    @Column(name = "kakao_id", nullable = false, unique = true, length = 50)
    private String kakaoId;

    /**
     * 사용자 닉네임
     */
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2-10자여야 합니다")
    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image", length = 500)
    private String profileImage;

    /**
     * 친구 초대용 고유 코드 (5자리)
     */
    @NotBlank(message = "친구 코드는 필수입니다")
    @Size(min = 5, max = 5, message = "친구 코드는 5자리여야 합니다")
    @Column(name = "friend_code", length = 5, nullable = false, unique = true)
    private String friendCode;

    /**
     * 계정 활성 상태
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // === 비즈니스 메서드 ===

    /**
     * 닉네임을 변경합니다
     */
    public void updateNickname(String newNickname) {
        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 빈 값일 수 없습니다");
        }
        if (newNickname.length() < 2 || newNickname.length() > 10) {
            throw new IllegalArgumentException("닉네임은 2-10자여야 합니다");
        }
        this.nickname = newNickname.trim();
    }

    /**
     * 프로필 이미지를 변경합니다
     */
    public void updateProfileImage(String newProfileImage) {
        this.profileImage = newProfileImage;
    }

    /**
     * 계정을 비활성화합니다
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 계정을 활성화합니다
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 활성 상태인지 확인합니다
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
} 