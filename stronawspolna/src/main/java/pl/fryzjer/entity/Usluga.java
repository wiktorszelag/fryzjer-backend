package pl.fryzjer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "USLUGA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usluga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usluga_id")
    private Integer uslugaId;

    @Column(name = "nazwa")
    private String nazwa;

    @Column(name = "opis")
    private String opis;

    @Column(name = "czas_trwania_min")
    private Integer czasTrwaniaMin;

    @Column(name = "cena_netto")
    private BigDecimal cenaNetto;

    @Column(name = "stawka_vat")
    private BigDecimal stawkaVat;

    @OneToMany(mappedBy = "usluga", cascade = CascadeType.ALL)
    private List<SzczegolyWizyty> szczegolyWizyt;

    @OneToMany(mappedBy = "usluga", cascade = CascadeType.ALL)
    private List<FryzjerUsluga> fryzjerUslugi;
}
