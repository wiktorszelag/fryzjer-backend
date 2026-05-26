package pl.fryzjer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fryzjer.entity.Urlop;

import java.util.List;

@Repository
public interface UrlopRepository extends JpaRepository<Urlop, Long> {
    List<Urlop> findByFryzjerId(Long fryzjerId);
}
