package com.voin.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CardCreateRequest {
    private Long formId;
    private Long coinId;
    private List<Long> keywordIds;
} 