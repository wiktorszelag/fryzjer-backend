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
    @Column(name = "UslugaID")
    private Integer uslugaId;

    @Column(name = "Nazwa")
    private String nazwa;

    @Column(name = "Opis")
    private String opis;

    @Column(name = "CzasTrwaniaMin")
    private Integer czasTrwaniaMin;

    @Column(name = "CenaNetto")
    private BigDecimal cenaNetto;

    @Column(name = "StawkaVAT")
    private BigDecimal stawkaVat;

    @OneToMany(mappedBy = "usluga", cascade = CascadeType.ALL)
    private List<SzczegolyWizyty> szczegolyWizyt;

    @OneToMany(mappedBy = "usluga", cascade = CascadeType.ALL)
    private List<FryzjerUsluga> fryzjerUslugi;
}
