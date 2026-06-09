package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// Klasa encji reprezentująca dane tabeli klient w bazie danych
// - automatyczne mapowanie pól na kolumny bazodanowe

@Data
@NoArgsConstructor
@Entity
@Table(name = "klient")
public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imie;
    private String nazwisko;
    private String telefon;

    @Column(name = "data_rejestracji")
    private LocalDate dataRejestracji;

    private String username;
}
