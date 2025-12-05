package pl.fryzjer.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SzczegolyWizytyDTO {
    private Integer szczegolyWizytyId;
    private Integer wizytaId;
    private Integer uslugaId;
    private BigDecimal cenaLacznaUslugi;
}
