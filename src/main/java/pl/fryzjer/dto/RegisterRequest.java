package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @Schema(description = "Nazwa użytkownika (login)", example = "jan_kowalski")
    private String username;
    
    @Schema(description = "Hasło użytkownika", example = "tajnehaslo123")
    private String password;
    
    @Schema(description = "Rola użytkownika (np. KLIENT, FRYZJER, ADMIN)", example = "KLIENT")
    private String rola;
}
