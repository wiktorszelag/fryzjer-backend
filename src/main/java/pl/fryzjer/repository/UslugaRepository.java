package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Usluga;

@Repository
public interface UslugaRepository extends JpaRepository<Usluga, Long> {
}
