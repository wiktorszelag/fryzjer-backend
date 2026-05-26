package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Fryzjer;

// Interfejs repozytorium do obsługi zapytań bazodanowych dla Fryzjer
// - automatyczne zapytania CRUD za pomocą Spring Data JPA

@Repository
public interface FryzjerRepository extends JpaRepository<Fryzjer, Long> {
}
