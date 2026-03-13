package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.FryzjerUslugaDTO;
import pl.fryzjer.service.FryzjerUslugaService;

import java.util.List;

@RestController
@RequestMapping("/api/fryzjer-uslugi")
@RequiredArgsConstructor
@Tag(name = "Fryzjer-Usługi", description = "Zarządzanie relacjami fryzjer-usługa")
@CrossOrigin(origins = "*")
public class FryzjerUslugaController {

    private final FryzjerUslugaService fryzjerUslugaService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie relacje fryzjer-usługa")
    public ResponseEntity<List<FryzjerUslugaDTO>> findAll() {
        return ResponseEntity.ok(fryzjerUslugaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz relację fryzjer-usługa po ID")
    public ResponseEntity<FryzjerUslugaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(fryzjerUslugaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nową relację fryzjer-usługa")
    public ResponseEntity<FryzjerUslugaDTO> create(@RequestBody FryzjerUslugaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fryzjerUslugaService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj relację fryzjer-usługa")
    public ResponseEntity<FryzjerUslugaDTO> update(@PathVariable Integer id, @RequestBody FryzjerUslugaDTO dto) {
        return ResponseEntity.ok(fryzjerUslugaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń relację fryzjer-usługa")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fryzjerUslugaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fryzjer/{fryzjerId}")
    @Operation(summary = "Pobierz usługi fryzjera")
    public ResponseEntity<List<FryzjerUslugaDTO>> findByFryzjerId(@PathVariable Integer fryzjerId) {
        return ResponseEntity.ok(fryzjerUslugaService.findByFryzjerId(fryzjerId));
    }

    @GetMapping("/usluga/{uslugaId}")
    @Operation(summary = "Pobierz fryzjerów oferujących usługę")
    public ResponseEntity<List<FryzjerUslugaDTO>> findByUslugaId(@PathVariable Integer uslugaId) {
        return ResponseEntity.ok(fryzjerUslugaService.findByUslugaId(uslugaId));
    }
}
