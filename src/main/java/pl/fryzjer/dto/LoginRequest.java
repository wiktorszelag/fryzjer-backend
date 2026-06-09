package pl.fryzjer.dto;

import lombok.Data;

// Klasa DTO LoginRequest służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
public class LoginRequest {
    private String username;
    private String password;
}
