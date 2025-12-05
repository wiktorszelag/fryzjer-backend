package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.FryzjerDTO;
import pl.fryzjer.service.FryzjerService;

import java.util.List;

@RestController
@RequestMapping("/api/fryzjerzy")
@RequiredArgsConstructor
@Tag(name = "Fryzjerzy", description = "Zarządzanie fryzjerami")
@CrossOrigin(origins = "*")
public class FryzjerController {

    private final FryzjerService fryzjerService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkich fryzjerów")
    public ResponseEntity<List<FryzjerDTO>> findAll() {
        return ResponseEntity.ok(fryzjerService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz fryzjera po ID")
    public ResponseEntity<FryzjerDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(fryzjerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nowego fryzjera")
    public ResponseEntity<FryzjerDTO> create(@RequestBody FryzjerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fryzjerService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj fryzjera")
    public ResponseEntity<FryzjerDTO> update(@PathVariable Integer id, @RequestBody FryzjerDTO dto) {
        return ResponseEntity.ok(fryzjerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń fryzjera")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fryzjerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/specjalizacja/{specjalizacja}")
    @Operation(summary = "Pobierz fryzjerów po specjalizacji")
    public ResponseEntity<List<FryzjerDTO>> findBySpecjalizacja(@PathVariable String specjalizacja) {
        return ResponseEntity.ok(fryzjerService.findBySpecjalizacja(specjalizacja));
    }
}
