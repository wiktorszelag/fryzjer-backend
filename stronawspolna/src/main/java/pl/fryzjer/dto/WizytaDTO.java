package pl.fryzjer.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WizytaDTO {
    private Integer wizytaId;
    private Integer klientId;
    private Integer fryzjerId;
    private LocalDateTime dataGodzinaRozpoczecia;
    private Integer czasTrwaniaCalkowity;
    private LocalDate dataRezerwacji;
    private List<Integer> uslugiIds;
}
