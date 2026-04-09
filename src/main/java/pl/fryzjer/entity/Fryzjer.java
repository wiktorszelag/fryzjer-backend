package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "fryzjer")
public class Fryzjer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Long id;

    private String imie;
    private String nazwisko;
    private String telefon;
=======
    @Column(name = "fryzjer_id")
    private Integer fryzjerId;

    @Column(name = "imie")
    private String imie;

    @Column(name = "nazwisko")
    private String nazwisko;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "specjalizacja")
>>>>>>> 1105a15 (frontendV1)
    private String specjalizacja;

    @Column(name = "data_zatrudnienia")
    private LocalDate dataZatrudnienia;
}
