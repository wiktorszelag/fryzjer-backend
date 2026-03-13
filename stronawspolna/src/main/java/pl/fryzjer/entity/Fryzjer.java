package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "FRYZJER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fryzjer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fryzjer_id")
    private Integer fryzjerId;

    @Column(name = "imie")
    private String imie;

    @Column(name = "nazwisko")
    private String nazwisko;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "specjalizacja")
    private String specjalizacja;

    @Column(name = "data_zatrudnienia")
    private LocalDate dataZatrudnienia;

    @OneToMany(mappedBy = "fryzjer", cascade = CascadeType.ALL)
    private List<Wizyta> wizyty;

    @OneToMany(mappedBy = "fryzjer", cascade = CascadeType.ALL)
    private List<FryzjerUsluga> fryzjerUslugi;
}
