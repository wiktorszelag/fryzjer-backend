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
<<<<<<< HEAD
    private Long id;

    @Column(name = "klientid")
    private Long klientId;

    @Column(name = "fryzjerid")
    private Long fryzjerId;
=======
    @Column(name = "wizyta_id")
    private Integer wizytaId;

    @ManyToOne
    @JoinColumn(name = "klient_id")
    private Klient klient;

    @ManyToOne
    @JoinColumn(name = "fryzjer_id")
    private Fryzjer fryzjer;
>>>>>>> 1105a15 (frontendV1)

    @Column(name = "data_godzina_rozpoczecia")
    private LocalDateTime dataGodzinaRozpoczecia;

<<<<<<< HEAD
    @Column(name = "czas_trwania_całkowity")
=======
    @Column(name = "czas_trwania_calkowity")
>>>>>>> 1105a15 (frontendV1)
    private Integer czasTrwaniaCalkowity;

    @Column(name = "data_rezerwacji")
    private LocalDate dataRezerwacji;
}
