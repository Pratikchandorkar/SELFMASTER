package com.selfmaster.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterRequest {
        @NotBlank(message = "First name is required")
        private String firstName;
        private String lastName;

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        private String confirmPassword;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginRequest {
        @NotBlank(message = "Email or username is required")
        private String usernameOrEmail;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private String refreshToken;
        private String type;
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private String role;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PasswordResetRequest {
        @NotBlank @Email
        private String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class NewPasswordRequest {
        @NotBlank private String token;
        @NotBlank @Size(min = 8) private String newPassword;
    }
}
