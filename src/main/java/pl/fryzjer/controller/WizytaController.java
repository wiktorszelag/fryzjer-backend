package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.WizytaDTO;
import pl.fryzjer.service.WizytaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wizyty")
@RequiredArgsConstructor
@Tag(name = "Wizyty", description = "Zarządzanie wizytami")
@CrossOrigin(origins = "*")
public class WizytaController {

    private final WizytaService wizytaService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie wizyty")
    public ResponseEntity<List<WizytaDTO>> findAll() {
        return ResponseEntity.ok(wizytaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz wizytę po ID")
    public ResponseEntity<WizytaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(wizytaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nową wizytę")
    public ResponseEntity<WizytaDTO> create(@RequestBody WizytaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wizytaService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj wizytę")
    public ResponseEntity<WizytaDTO> update(@PathVariable Integer id, @RequestBody WizytaDTO dto) {
        return ResponseEntity.ok(wizytaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń wizytę")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        wizytaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/klient/{klientId}")
    @Operation(summary = "Pobierz wizyty klienta")
    public ResponseEntity<List<WizytaDTO>> findByKlientId(@PathVariable Integer klientId) {
        return ResponseEntity.ok(wizytaService.findByKlientId(klientId));
    }

    @GetMapping("/fryzjer/{fryzjerId}")
    @Operation(summary = "Pobierz wizyty fryzjera")
    public ResponseEntity<List<WizytaDTO>> findByFryzjerId(@PathVariable Integer fryzjerId) {
        return ResponseEntity.ok(wizytaService.findByFryzjerId(fryzjerId));
    }

    @GetMapping("/fryzjer/{fryzjerId}/zakres")
    @Operation(summary = "Pobierz wizyty fryzjera w zakresie dat")
    public ResponseEntity<List<WizytaDTO>> findByFryzjerIdAndDateRange(
            @PathVariable Integer fryzjerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(wizytaService.findByFryzjerIdAndDateRange(fryzjerId, start, end));
    }
}
