package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Wizyta;

import java.util.List;

@Repository
public interface WizytaRepository extends JpaRepository<Wizyta, Long> {
    List<Wizyta> findByKlientId(Long klientId);
    List<Wizyta> findByFryzjerId(Long fryzjerId);
}
