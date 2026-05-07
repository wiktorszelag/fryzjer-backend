package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Uzytkownik;

import java.util.Optional;

@Repository
public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Long> {
    Optional<Uzytkownik> findByUsername(String username);
}
