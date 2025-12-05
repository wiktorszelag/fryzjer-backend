package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "KLIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KlientID")
    private Integer klientId;

    @Column(name = "Imie")
    private String imie;

    @Column(name = "Nazwisko")
    private String nazwisko;

    @Column(name = "Telefon")
    private String telefon;

    @Column(name = "DataRejestracji")
    private LocalDate dataRejestracji;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Wizyta> wizyty;
}
