package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "wizyta")
public class Wizyta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "klientid")
    private Long klientId;

    @Column(name = "fryzjerid")
    private Long fryzjerId;

    @Column(name = "data_godzina_rozpoczecia")
    private LocalDateTime dataGodzinaRozpoczecia;

    @Column(name = "czas_trwania_całkowity")
    private Integer czasTrwaniaCalkowity;

    @Column(name = "data_rezerwacji")
    private LocalDate dataRezerwacji;
}
