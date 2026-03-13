package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.SzczegolyWizyty;

import java.util.List;

@Repository
public interface SzczegolyWizytyRepository extends JpaRepository<SzczegolyWizyty, Integer> {
    List<SzczegolyWizyty> findByWizytaWizytaId(Integer wizytaId);
}
