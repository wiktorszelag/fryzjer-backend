package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.entity.Harmonogram;
import pl.fryzjer.repository.HarmonogramRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/harmonogram")
public class HarmonogramController {

    private final HarmonogramRepository harmonogramRepository;

    public HarmonogramController(HarmonogramRepository harmonogramRepository) {
        this.harmonogramRepository = harmonogramRepository;
    }

    @GetMapping
    public ResponseEntity<List<Harmonogram>> getAll() {
        return ResponseEntity.ok(harmonogramRepository.findAll());
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<List<Harmonogram>> getByData(@PathVariable LocalDate data) {
        return ResponseEntity.ok(harmonogramRepository.findByData(data));
    }

    @GetMapping("/fryzjer/{fryzjerId}")
    public ResponseEntity<List<Harmonogram>> getByFryzjer(@PathVariable Long fryzjerId) {
        return ResponseEntity.ok(harmonogramRepository.findByFryzjerId(fryzjerId));
    }

    @PostMapping
    public ResponseEntity<Harmonogram> create(@RequestBody Harmonogram harmonogram) {
        // Usuń stare zmiany dla tego fryzjera na ten dzień, jeśli nadpisujemy grafik
        List<Harmonogram> istniejace = harmonogramRepository.findByFryzjerIdAndData(harmonogram.getFryzjerId(), harmonogram.getData());
        if (!istniejace.isEmpty()) {
            harmonogramRepository.deleteAll(istniejace);
        }
        Harmonogram saved = harmonogramRepository.save(harmonogram);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (harmonogramRepository.existsById(id)) {
            harmonogramRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
