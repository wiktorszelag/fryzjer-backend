package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// Klasa DTO RegisterRequest służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
public class RegisterRequest {
    
    @Schema(description = "Nazwa użytkownika (login)", example = "jan_kowalski")
    private String username;
    
    @Schema(description = "Hasło użytkownika", example = "tajnehaslo123")
    private String password;
    
    @Schema(description = "Rola użytkownika (np. KLIENT, FRYZJER, ADMIN)", example = "KLIENT")
    private String rola;

    @Schema(description = "Adres email", example = "jan@example.com")
    private String email;

    @Schema(description = "Numer telefonu", example = "123456789")
    @jakarta.validation.constraints.Pattern(regexp = "^\\d{9}$", message = "Numer telefonu musi składać się z dokładnie 9 cyfr")
    private String telefon;
}
