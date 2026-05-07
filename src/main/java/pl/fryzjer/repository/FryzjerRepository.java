package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Fryzjer;

@Repository
public interface FryzjerRepository extends JpaRepository<Fryzjer, Long> {
}
