package com.ayush.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private String error;
    private String path;
    private Map<String,String> validationErrors;
    private LocalDateTime timestamp;
    private String method;


}
