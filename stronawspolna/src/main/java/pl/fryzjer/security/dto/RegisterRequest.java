package pl.fryzjer.security.dto;

public record RegisterRequest(String username, String password, String rola, String kodDostepu) {}
