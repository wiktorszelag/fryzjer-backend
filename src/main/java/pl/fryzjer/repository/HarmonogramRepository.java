package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fryzjer.entity.Harmonogram;
import java.time.LocalDate;
import java.util.List;

// Interfejs repozytorium do obsługi zapytań bazodanowych dla Harmonogram
// - automatyczne zapytania CRUD za pomocą Spring Data JPA

public interface HarmonogramRepository extends JpaRepository<Harmonogram, Long> {
    List<Harmonogram> findByFryzjerId(Long fryzjerId);
    List<Harmonogram> findByData(LocalDate data);
    List<Harmonogram> findByFryzjerIdAndData(Long fryzjerId, LocalDate data);
}
