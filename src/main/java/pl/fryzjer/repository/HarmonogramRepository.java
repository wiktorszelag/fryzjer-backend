package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fryzjer.entity.Harmonogram;
import java.time.LocalDate;
import java.util.List;

public interface HarmonogramRepository extends JpaRepository<Harmonogram, Long> {
    List<Harmonogram> findByFryzjerId(Long fryzjerId);
    List<Harmonogram> findByData(LocalDate data);
    List<Harmonogram> findByFryzjerIdAndData(Long fryzjerId, LocalDate data);
}
