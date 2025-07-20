package com.voin.dto.request;

import com.voin.dto.common.GptMessage;
import lombok.Data;
import java.util.List;

@Data
public class GptRequest {
    private String model;
    private List<GptMessage> messages;
}