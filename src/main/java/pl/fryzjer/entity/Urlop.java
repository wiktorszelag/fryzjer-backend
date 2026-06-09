package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// Klasa encji reprezentująca dane tabeli urlop w bazie danych
// - automatyczne mapowanie pól na kolumny bazodanowe

@Data
@NoArgsConstructor
@Entity
@Table(name = "urlop")
public class Urlop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fryzjer_id", nullable = false)
    private Long fryzjerId;

    @Column(name = "data_od", nullable = false)
    private LocalDate dataOd;

    @Column(name = "data_do", nullable = false)
    private LocalDate dataDo;
}
