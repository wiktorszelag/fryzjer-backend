package pl.fryzjer.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KlientDTO {
    private Long id;
    private String imie;
    private String nazwisko;
    private String telefon;
    private LocalDateTime dataRejestracji;
}
