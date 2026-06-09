package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Usluga;

// Interfejs repozytorium do obsługi zapytań bazodanowych dla Usluga
// - automatyczne zapytania CRUD za pomocą Spring Data JPA

@Repository
public interface UslugaRepository extends JpaRepository<Usluga, Long> {
}
