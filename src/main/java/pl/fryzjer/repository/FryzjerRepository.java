package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Fryzjer;

import java.util.List;

@Repository
public interface FryzjerRepository extends JpaRepository<Fryzjer, Integer> {
    List<Fryzjer> findBySpecjalizacja(String specjalizacja);
}
