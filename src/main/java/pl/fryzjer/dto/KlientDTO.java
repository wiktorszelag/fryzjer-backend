package pl.fryzjer.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KlientDTO {
    private Integer klientId;
    private String imie;
    private String nazwisko;
    private String telefon;
    private LocalDate dataRejestracji;
}
