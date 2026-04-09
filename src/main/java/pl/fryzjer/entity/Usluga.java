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
<<<<<<< HEAD
    private Long id;

    private String nazwa;
=======
    @Column(name = "usluga_id")
    private Integer uslugaId;

    @Column(name = "nazwa")
    private String nazwa;

    @Column(name = "opis")
>>>>>>> 1105a15 (frontendV1)
    private String opis;

    @Column(name = "czas_trwania_min")
    private Integer czasTrwaniaMin;

    @Column(name = "cena_netto")
    private BigDecimal cenaNetto;

<<<<<<< HEAD
    private BigDecimal stawkavat;
=======
    @Column(name = "stawka_vat")
    private BigDecimal stawkaVat;

    @OneToMany(mappedBy = "usluga", cascade = CascadeType.ALL)
    private List<SzczegolyWizyty> szczegolyWizyt;

    @OneToMany(mappedBy = "usluga", cascade = CascadeType.ALL)
    private List<FryzjerUsluga> fryzjerUslugi;
>>>>>>> 1105a15 (frontendV1)
}
