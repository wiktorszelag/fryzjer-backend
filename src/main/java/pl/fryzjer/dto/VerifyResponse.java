package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

// Klasa DTO VerifyResponse służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
@AllArgsConstructor
public class VerifyResponse {
    @Schema(description = "Status weryfikacji. TRUE = nienaruszony, autentyczny. FALSE = zmodyfikowany lub błędny podpis.")
    private boolean isValid;
    
    @Schema(description = "Opis wyniku weryfikacji.")
    private String message;
}
