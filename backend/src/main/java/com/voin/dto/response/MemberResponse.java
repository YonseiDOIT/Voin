package com.voin.dto.response;

import com.voin.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private UUID id;
    private String kakaoId;
    private String nickname;
    private String profileImage;
    private String friendCode;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .kakaoId(member.getKakaoId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .friendCode(member.getFriendCode())
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public static MemberResponse fromWithoutSensitiveInfo(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .build();
    }
} 