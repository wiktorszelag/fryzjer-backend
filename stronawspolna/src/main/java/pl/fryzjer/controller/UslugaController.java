package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.UslugaDTO;
import pl.fryzjer.service.UslugaService;

import java.util.List;

@RestController
@RequestMapping("/api/uslugi")
@RequiredArgsConstructor
@Tag(name = "Usługi", description = "Zarządzanie usługami")
@CrossOrigin(origins = "*")
public class UslugaController {

    private final UslugaService uslugaService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie usługi")
    public ResponseEntity<List<UslugaDTO>> findAll() {
        return ResponseEntity.ok(uslugaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz usługę po ID")
    public ResponseEntity<UslugaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(uslugaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nową usługę")
    public ResponseEntity<UslugaDTO> create(@RequestBody UslugaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(uslugaService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj usługę")
    public ResponseEntity<UslugaDTO> update(@PathVariable Integer id, @RequestBody UslugaDTO dto) {
        return ResponseEntity.ok(uslugaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń usługę")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        uslugaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
