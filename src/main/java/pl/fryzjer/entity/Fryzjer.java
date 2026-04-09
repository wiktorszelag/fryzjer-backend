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
    private Long id;

    private String imie;
    private String nazwisko;
    private String telefon;
    private String specjalizacja;

    @Column(name = "data_zatrudnienia")
    private LocalDate dataZatrudnienia;
}
