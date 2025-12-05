package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.FryzjerDTO;
import pl.fryzjer.entity.Fryzjer;
import pl.fryzjer.repository.FryzjerRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fryzjerzy")
public class FryzjerController {

    private final FryzjerRepository fryzjerRepository;

    public FryzjerController(FryzjerRepository fryzjerRepository) {
        this.fryzjerRepository = fryzjerRepository;
    }

    @GetMapping
    public ResponseEntity<List<FryzjerDTO>> getAll() {
        List<FryzjerDTO> fryzjerzy = fryzjerRepository.findAll().stream().map(f -> {
            FryzjerDTO dto = new FryzjerDTO();
            dto.setId(f.getId());
            dto.setImie(f.getImie());
            dto.setNazwisko(f.getNazwisko());
            dto.setTelefon(f.getTelefon());
            dto.setSpecjalizacja(f.getSpecjalizacja());
            dto.setDataZatrudnienia(f.getDataZatrudnienia());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(fryzjerzy);
    }

    @PostMapping
    public ResponseEntity<FryzjerDTO> create(@RequestBody FryzjerDTO dto) {
        Fryzjer fryzjer = new Fryzjer();
        fryzjer.setImie(dto.getImie());
        fryzjer.setNazwisko(dto.getNazwisko());
        fryzjer.setTelefon(dto.getTelefon());
        fryzjer.setSpecjalizacja(dto.getSpecjalizacja());
        fryzjer.setDataZatrudnienia(dto.getDataZatrudnienia());

        fryzjer = fryzjerRepository.save(fryzjer);
        dto.setId(fryzjer.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FryzjerDTO> update(@PathVariable Long id, @RequestBody FryzjerDTO dto) {
        return fryzjerRepository.findById(id).map(fryzjer -> {
            fryzjer.setImie(dto.getImie());
            fryzjer.setNazwisko(dto.getNazwisko());
            fryzjer.setTelefon(dto.getTelefon());
            fryzjer.setSpecjalizacja(dto.getSpecjalizacja());
            if (dto.getDataZatrudnienia() != null) {
                fryzjer.setDataZatrudnienia(dto.getDataZatrudnienia());
            }
            fryzjerRepository.save(fryzjer);
            dto.setId(fryzjer.getId());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (fryzjerRepository.existsById(id)) {
            fryzjerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
