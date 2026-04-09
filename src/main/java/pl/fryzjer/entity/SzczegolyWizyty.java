package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SZCZEGOLY_WIZYTY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SzczegolyWizyty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "szczegoly_wizyty_id")
    private Integer szczegolyWizytyId;

    @ManyToOne
    @JoinColumn(name = "wizyta_id")
    private Wizyta wizyta;

    @ManyToOne
    @JoinColumn(name = "usluga_id")
    private Usluga usluga;

    @Column(name = "cena_laczna_uslugi")
    private BigDecimal cenaLacznaUslugi;
}
