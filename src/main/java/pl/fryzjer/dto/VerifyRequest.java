package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// Klasa DTO VerifyRequest służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
public class VerifyRequest {
    @Schema(description = "Oryginalna lub zmodyfikowana treść dokumentu", example = "Umowa o pracę z Janem Kowalskim. Wypłata: 5000 PLN.")
    private String documentContent;

    @Schema(description = "Podpis cyfrowy do weryfikacji (uzyskany wcześniej z endpointu /sign)")
    private String signature;
}
