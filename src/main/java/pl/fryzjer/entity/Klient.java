package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "klient")
public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Long id;

    private String imie;
    private String nazwisko;
=======
    @Column(name = "klient_id")
    private Integer klientId;

    @Column(name = "imie")
    private String imie;

    @Column(name = "nazwisko")
    private String nazwisko;

    @Column(name = "telefon")
>>>>>>> 1105a15 (frontendV1)
    private String telefon;

    @Column(name = "data_rejestracji")
    private LocalDate dataRejestracji;
}
