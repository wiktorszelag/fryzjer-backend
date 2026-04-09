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
    @Column(name = "fryzjer_usluga_id")
    private Integer fryzjerUslugaId;

    @ManyToOne
    @JoinColumn(name = "fryzjer_id")
    private Fryzjer fryzjer;

    @ManyToOne
    @JoinColumn(name = "usluga_id")
    private Usluga usluga;

    @Column(name = "cena_fryzjera")
    private BigDecimal cenaFryzjera;
}
