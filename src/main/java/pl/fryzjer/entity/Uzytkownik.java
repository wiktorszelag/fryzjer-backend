package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Klasa encji reprezentująca dane tabeli uzytkownik w bazie danych
// - automatyczne mapowanie pól na kolumny bazodanowe

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "uzytkownik")
public class Uzytkownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rola;

    @Column
    private String email;

    @Column
    private String telefon;
}
