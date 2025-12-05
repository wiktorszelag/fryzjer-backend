package pl.fryzjer.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FryzjerUslugaDTO {
    private Integer fryzjerUslugaId;
    private Integer fryzjerId;
    private Integer uslugaId;
    private BigDecimal cenaFryzjera;
}
