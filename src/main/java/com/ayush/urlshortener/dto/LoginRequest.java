package com.ayush.urlshortener.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor

@Data
public class LoginRequest {

    @Email(message = "Enter a Valid Email")
    @NotBlank
    private String email;

    @Size(min = 8, message = "Password must consist of at least 8 characters")
    @NotBlank(message = "Password is required")
    private String password;

}
