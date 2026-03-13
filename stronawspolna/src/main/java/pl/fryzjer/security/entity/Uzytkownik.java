package pl.fryzjer.security.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UZYTKOWNIK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Uzytkownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uzytkownik_id")
    private Integer id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "aktywny")
    private Boolean aktywny;

    @Enumerated(EnumType.STRING)
    @Column(name = "rola", nullable = false)
    private Rola rola;
}
