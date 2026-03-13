package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "WIZYTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wizyta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wizyta_id")
    private Integer wizytaId;

    @ManyToOne
    @JoinColumn(name = "klient_id")
    private Klient klient;

    @ManyToOne
    @JoinColumn(name = "fryzjer_id")
    private Fryzjer fryzjer;

    @Column(name = "data_godzina_rozpoczecia")
    private LocalDateTime dataGodzinaRozpoczecia;

    @Column(name = "czas_trwania_calkowity")
    private Integer czasTrwaniaCalkowity;

    @Column(name = "data_rezerwacji")
    private LocalDate dataRezerwacji;

    @OneToMany(mappedBy = "wizyta", cascade = CascadeType.ALL)
    private List<SzczegolyWizyty> szczegolyWizyty;
}
