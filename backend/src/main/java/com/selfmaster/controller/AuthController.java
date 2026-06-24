package com.selfmaster.controller;

import com.selfmaster.dto.ApiResponse;
import com.selfmaster.dto.AuthDto;
import com.selfmaster.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/auth/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new AuthDto.LoginRequest());
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new AuthDto.RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/auth/login")
    public String loginSubmit(@ModelAttribute AuthDto.LoginRequest loginRequest,
                              HttpServletResponse response, Model model) {
        try {
            AuthDto.AuthResponse authResponse = authService.login(loginRequest);
            addJwtCookie(response, authResponse.getToken());
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials. Please try again.");
            model.addAttribute("loginRequest", loginRequest);
            return "auth/login";
        }
    }

    @PostMapping("/auth/register")
    public String registerSubmit(@ModelAttribute @Valid AuthDto.RegisterRequest registerRequest,
                                 HttpServletResponse response, Model model) {
        try {
            AuthDto.AuthResponse authResponse = authService.register(registerRequest);
            addJwtCookie(response, authResponse.getToken());
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerRequest", registerRequest);
            return "auth/register";
        }
    }

    @GetMapping("/auth/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt_token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/auth/login?logout=true";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/auth/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String email, HttpServletRequest request, Model model) {
        try {
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            String portString = (serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort;
            String urlPrefix = scheme + "://" + serverName + portString + contextPath + "/auth/reset-password";

            authService.initiatePasswordReset(email, urlPrefix);
            model.addAttribute("success", "A password reset link has been sent to your email.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
        }
        return "auth/forgot-password";
    }

    @GetMapping("/auth/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        if (!authService.validateResetToken(token)) {
            return "redirect:/auth/login?error=" + java.net.URLEncoder.encode("Invalid or expired password reset token.", java.nio.charset.StandardCharsets.UTF_8);
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/auth/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        if (password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        try {
            authService.resetPassword(token, password);
            return "redirect:/auth/login?success=" + java.net.URLEncoder.encode("Password reset successful. Please login with your new password.", java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> apiRegister(
            @RequestBody @Valid AuthDto.RegisterRequest request) {
        AuthDto.AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authResponse));
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> apiLogin(
            @RequestBody @Valid AuthDto.LoginRequest request) {
        AuthDto.AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/api/auth/forgot-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody AuthDto.PasswordResetRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Password reset link sent to your email"));
    }

    @PostMapping("/api/auth/reset-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody AuthDto.NewPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
    }

    private void addJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); 
        cookie.setPath("/");
        cookie.setMaxAge(86400); 
        response.addCookie(cookie);
    }
}
