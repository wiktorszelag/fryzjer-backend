package pl.fryzjer.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FryzjerDTO {
    private Long id;
    private String imie;
    private String nazwisko;
    private String telefon;
    private String specjalizacja;
    private LocalDate dataZatrudnienia;
}
