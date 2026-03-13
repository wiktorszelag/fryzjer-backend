package pl.fryzjer.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.fryzjer.security.entity.Uzytkownik;

import java.util.Optional;

public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Integer> {
    Optional<Uzytkownik> findByUsername(String username);
}
