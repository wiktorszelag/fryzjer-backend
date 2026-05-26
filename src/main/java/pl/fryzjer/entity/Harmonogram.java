package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

// Klasa encji reprezentująca dane tabeli harmonogram w bazie danych
// - automatyczne mapowanie pól na kolumny bazodanowe

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "harmonogram")
public class Harmonogram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fryzjer_id", nullable = false)
    private Long fryzjerId;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "godzina_od", nullable = false)
    private LocalTime godzinaOd;

    @Column(name = "godzina_do", nullable = false)
    private LocalTime godzinaDo;
}
