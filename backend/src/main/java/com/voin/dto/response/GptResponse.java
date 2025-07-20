package com.voin.dto.response;


import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GptResponse {
    private List<Map<String, Object>> choices;
}