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
    @Column(name = "FryzjerID")
    private Integer fryzjerId;

    @Column(name = "Imie")
    private String imie;

    @Column(name = "Nazwisko")
    private String nazwisko;

    @Column(name = "Telefon")
    private String telefon;

    @Column(name = "Specjalizacja")
    private String specjalizacja;

    @Column(name = "DataZatrudnienia")
    private LocalDate dataZatrudnienia;

    @OneToMany(mappedBy = "fryzjer", cascade = CascadeType.ALL)
    private List<Wizyta> wizyty;

    @OneToMany(mappedBy = "fryzjer", cascade = CascadeType.ALL)
    private List<FryzjerUsluga> fryzjerUslugi;
}
