package com.ayush.urlshortener.dto;

import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateExpirationRequest {

    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;

}
