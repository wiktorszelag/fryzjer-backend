package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Klient;

import java.util.Optional;

@Repository
public interface KlientRepository extends JpaRepository<Klient, Integer> {
    Optional<Klient> findByTelefon(String telefon);
}
