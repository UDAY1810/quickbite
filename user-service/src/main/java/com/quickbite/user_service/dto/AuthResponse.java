package com.quickbite.user_service.dto;

public record AuthResponse(String token, Long userId, String fullName, String role) {}