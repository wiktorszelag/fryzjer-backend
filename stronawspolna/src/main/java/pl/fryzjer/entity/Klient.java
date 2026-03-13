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
    @Column(name = "klient_id")
    private Integer klientId;

    @Column(name = "imie")
    private String imie;

    @Column(name = "nazwisko")
    private String nazwisko;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "data_rejestracji")
    private LocalDate dataRejestracji;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Wizyta> wizyty;
}
