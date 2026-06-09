package pl.fryzjer.dto;

import lombok.Data;
import java.math.BigDecimal;

// Klasa DTO UslugaDTO służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
public class UslugaDTO {
    private Long id;
    private String nazwa;
    private String opis;
    private Integer czasTrwaniaMin;
    private BigDecimal cenaNetto;
    private BigDecimal stawkaVat;
}
