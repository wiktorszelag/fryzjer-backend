package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Klient;

import java.util.Optional;

// Interfejs repozytorium do obsługi zapytań bazodanowych dla Klient
// - automatyczne zapytania CRUD za pomocą Spring Data JPA

@Repository
public interface KlientRepository extends JpaRepository<Klient, Long> {
    Optional<Klient> findByUsername(String username);
}
