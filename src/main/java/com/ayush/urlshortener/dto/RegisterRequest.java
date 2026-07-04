package com.ayush.urlshortener.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class RegisterRequest {
    @NotBlank(message = "Name is Required")
    private String name;

    @Email(message  = "Valid email required")
    @NotBlank
    private String email;

    @Size(min = 8, message = "Password must consist 8 characters")
    @NotBlank(message = "Password is Required")
    private String password;



}
