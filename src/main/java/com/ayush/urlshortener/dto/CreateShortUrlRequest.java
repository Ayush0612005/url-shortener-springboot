package com.ayush.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
public class CreateShortUrlRequest {

    @NotBlank
    @URL
    private String originalUrl;

    private LocalDateTime expiresAt;

}
