package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Wizyta;

import java.util.List;

// Interfejs repozytorium do obsługi zapytań bazodanowych dla Wizyta
// - automatyczne zapytania CRUD za pomocą Spring Data JPA

@Repository
public interface WizytaRepository extends JpaRepository<Wizyta, Long> {
    List<Wizyta> findByKlientId(Long klientId);
    List<Wizyta> findByFryzjerId(Long fryzjerId);
}
