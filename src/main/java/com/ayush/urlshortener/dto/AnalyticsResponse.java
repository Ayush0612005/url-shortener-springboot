package com.ayush.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnalyticsResponse {

    private String originalUrl;

    private String shortCode;

    private String shortUrl;

    private long clickCount;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime lastAccessedAt;

    private String status;
}
