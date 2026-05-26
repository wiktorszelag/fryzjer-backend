package pl.fryzjer.dto;

import lombok.Data;
import java.time.LocalDateTime;

// Klasa DTO KlientDTO służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
public class KlientDTO {
    private Long id;
    private String imie;
    private String nazwisko;
    private String telefon;
    private LocalDateTime dataRejestracji;
}
