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
    @Column(name = "WizytaID")
    private Integer wizytaId;

    @ManyToOne
    @JoinColumn(name = "KlientID")
    private Klient klient;

    @ManyToOne
    @JoinColumn(name = "FryzjerID")
    private Fryzjer fryzjer;

    @Column(name = "DataGodzinaRozpoczecia")
    private LocalDateTime dataGodzinaRozpoczecia;

    @Column(name = "CzasTrwaniaCałkowity")
    private Integer czasTrwaniaCalkowity;

    @Column(name = "DataRezerwacji")
    private LocalDate dataRezerwacji;

    @OneToMany(mappedBy = "wizyta", cascade = CascadeType.ALL)
    private List<SzczegolyWizyty> szczegolyWizyty;
}
