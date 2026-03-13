package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.FryzjerUsluga;

import java.util.List;

@Repository
public interface FryzjerUslugaRepository extends JpaRepository<FryzjerUsluga, Integer> {
    List<FryzjerUsluga> findByFryzjerFryzjerId(Integer fryzjerId);
    List<FryzjerUsluga> findByUslugaUslugaId(Integer uslugaId);
}
