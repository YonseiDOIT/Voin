package com.voin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카카오 사용자 정보 응답")
public class KakaoUserResponse {

    @Schema(description = "카카오 사용자 ID", example = "4327485143")
    private Long id;

    @Schema(description = "사용자 닉네임", example = "박규민")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://k.kakaocdn.net/dn/cwJdOi/btsLAA9fVoq/7IQ8cPTFPu3cQGpPFQeTrK/img_640x640.jpg")
    private String profileImage;

    @Schema(description = "썸네일 이미지 URL", example = "http://k.kakaocdn.net/dn/cwJdOi/btsLAA9fVoq/7IQ8cPTFPu3cQGpPFQeTrK/img_110x110.jpg")
    private String thumbnailImage;

    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "성별", example = "male")
    private String gender;

    @Schema(description = "연령대", example = "20~29")
    private String ageRange;

    @Schema(description = "생년월일", example = "1201")
    private String birthday;
} 