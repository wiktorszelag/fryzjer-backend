package pl.fryzjer.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UslugaDTO {
    private Long id;
    private String nazwa;
    private String opis;
    private Integer czasTrwaniaMin;
    private BigDecimal cenaNetto;
    private BigDecimal stawkaVat;
}
