package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usluga")
public class Usluga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nazwa;
    private String opis;

    @Column(name = "czas_trwania_min")
    private Integer czasTrwaniaMin;

    @Column(name = "cena_netto")
    private BigDecimal cenaNetto;

    private BigDecimal stawkavat;
}
