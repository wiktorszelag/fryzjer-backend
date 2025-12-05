package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "FRYZJER_USLUGA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FryzjerUsluga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FryzjerUslugaID")
    private Integer fryzjerUslugaId;

    @ManyToOne
    @JoinColumn(name = "FryzjerID")
    private Fryzjer fryzjer;

    @ManyToOne
    @JoinColumn(name = "UslugaID")
    private Usluga usluga;

    @Column(name = "CenaFryzjera")
    private BigDecimal cenaFryzjera;
}
