package pl.fryzjer.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UslugaDTO {
    private Integer uslugaId;
    private String nazwa;
    private String opis;
    private Integer czasTrwaniaMin;
    private BigDecimal cenaNetto;
    private BigDecimal stawkaVat;
}
