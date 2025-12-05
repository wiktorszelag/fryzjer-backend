package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Wizyta;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WizytaRepository extends JpaRepository<Wizyta, Integer> {
    List<Wizyta> findByKlientKlientId(Integer klientId);
    List<Wizyta> findByFryzjerFryzjerId(Integer fryzjerId);
    List<Wizyta> findByFryzjerFryzjerIdAndDataGodzinaRozpoczeciaBetween(
            Integer fryzjerId, LocalDateTime start, LocalDateTime end);
}
