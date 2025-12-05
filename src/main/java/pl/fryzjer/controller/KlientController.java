package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.KlientDTO;
import pl.fryzjer.service.KlientService;

import java.util.List;

@RestController
@RequestMapping("/api/klienci")
@RequiredArgsConstructor
@Tag(name = "Klienci", description = "Zarządzanie klientami")
@CrossOrigin(origins = "*")
public class KlientController {

    private final KlientService klientService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkich klientów")
    public ResponseEntity<List<KlientDTO>> findAll() {
        return ResponseEntity.ok(klientService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz klienta po ID")
    public ResponseEntity<KlientDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(klientService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nowego klienta")
    public ResponseEntity<KlientDTO> create(@RequestBody KlientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(klientService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj klienta")
    public ResponseEntity<KlientDTO> update(@PathVariable Integer id, @RequestBody KlientDTO dto) {
        return ResponseEntity.ok(klientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń klienta")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        klientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
