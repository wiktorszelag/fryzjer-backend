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
    @Column(name = "SzczegolyWizytyID")
    private Integer szczegolyWizytyId;

    @ManyToOne
    @JoinColumn(name = "WizytaID")
    private Wizyta wizyta;

    @ManyToOne
    @JoinColumn(name = "UslugaID")
    private Usluga usluga;

    @Column(name = "CenaLacznaUslugi")
    private BigDecimal cenaLacznaUslugi;
}
