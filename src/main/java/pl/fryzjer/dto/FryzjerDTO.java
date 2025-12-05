package pl.fryzjer.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FryzjerDTO {
    private Integer fryzjerId;
    private String imie;
    private String nazwisko;
    private String telefon;
    private String specjalizacja;
    private LocalDate dataZatrudnienia;
}
