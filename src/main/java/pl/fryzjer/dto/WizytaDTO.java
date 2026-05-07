package pl.fryzjer.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WizytaDTO {
    private Long id;
    private Long klientId;
    private Long fryzjerId;
    private LocalDateTime dataGodzinaRozpoczecia;
    private Integer czasTrwaniaCalkowity;
    private List<Long> uslugiIds;
}
