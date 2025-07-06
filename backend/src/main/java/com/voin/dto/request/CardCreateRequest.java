package com.voin.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardCreateRequest {
    private Long formId;
    private Long coinId;
    private List<Long> keywordIds;
} 