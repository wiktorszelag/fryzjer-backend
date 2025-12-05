package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.SzczegolyWizytyDTO;
import pl.fryzjer.service.SzczegolyWizytyService;

import java.util.List;

@RestController
@RequestMapping("/api/szczegoly-wizyty")
@RequiredArgsConstructor
@Tag(name = "Szczegóły wizyty", description = "Zarządzanie szczegółami wizyt")
@CrossOrigin(origins = "*")
public class SzczegolyWizytyController {

    private final SzczegolyWizytyService szczegolyWizytyService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie szczegóły wizyt")
    public ResponseEntity<List<SzczegolyWizytyDTO>> findAll() {
        return ResponseEntity.ok(szczegolyWizytyService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz szczegóły wizyty po ID")
    public ResponseEntity<SzczegolyWizytyDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(szczegolyWizytyService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nowe szczegóły wizyty")
    public ResponseEntity<SzczegolyWizytyDTO> create(@RequestBody SzczegolyWizytyDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(szczegolyWizytyService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj szczegóły wizyty")
    public ResponseEntity<SzczegolyWizytyDTO> update(@PathVariable Integer id, @RequestBody SzczegolyWizytyDTO dto) {
        return ResponseEntity.ok(szczegolyWizytyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń szczegóły wizyty")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        szczegolyWizytyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wizyta/{wizytaId}")
    @Operation(summary = "Pobierz szczegóły dla wizyty")
    public ResponseEntity<List<SzczegolyWizytyDTO>> findByWizytaId(@PathVariable Integer wizytaId) {
        return ResponseEntity.ok(szczegolyWizytyService.findByWizytaId(wizytaId));
    }
}
