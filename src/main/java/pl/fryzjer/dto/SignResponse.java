package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignResponse {
    @Schema(description = "Wygenerowany podpis cyfrowy (Base64)")
    private String signature;
}
