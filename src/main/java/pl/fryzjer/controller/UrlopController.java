package pl.fryzjer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.entity.Urlop;
import pl.fryzjer.repository.UrlopRepository;

import java.util.List;

// Kontroler zarządzania urlopami fryzjerów
// - planowanie urlopów dla pracowników
// - sprawdzanie aktywnych urlopów

@RestController
@RequestMapping("/api/urlopy")
public class UrlopController {

    private final UrlopRepository urlopRepository;

    public UrlopController(UrlopRepository urlopRepository) {
        this.urlopRepository = urlopRepository;
    }

    @GetMapping("/fryzjer/{fryzjerId}")
    public ResponseEntity<List<Urlop>> getUrlopyByFryzjer(@PathVariable Long fryzjerId) {
        return ResponseEntity.ok(urlopRepository.findByFryzjerId(fryzjerId));
    }

    @PostMapping
    public ResponseEntity<Urlop> dodajUrlop(@RequestBody Urlop urlop) {
        return ResponseEntity.ok(urlopRepository.save(urlop));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> usunUrlop(@PathVariable Long id) {
        if (urlopRepository.existsById(id)) {
            urlopRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
