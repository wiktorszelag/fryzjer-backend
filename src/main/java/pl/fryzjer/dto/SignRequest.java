package pl.fryzjer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignRequest {
    @Schema(description = "Treść dokumentu do podpisania (np. treść umowy)", example = "Umowa o pracę z Janem Kowalskim. Wypłata: 5000 PLN.")
    private String documentContent;
}
