package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

// Klasa DTO AuthResponse służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
@AllArgsConstructor
public class AuthResponse {
    
    @Schema(description = "Nazwa użytkownika po zalogowaniu", example = "jan_kowalski")
    private String username;
    
    @Schema(description = "Rola przypisana do użytkownika", example = "KLIENT")
    private String rola;
    
    @Schema(description = "Komunikat zwrotny", example = "Zalogowano pomyślnie.")
    private String message;
    
    @Schema(description = "Token JWT używany do autoryzacji w kolejnych zapytaniach", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}
