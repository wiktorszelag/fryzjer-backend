package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.UslugaDTO;
import pl.fryzjer.entity.Usluga;
import pl.fryzjer.repository.UslugaRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/uslugi")
public class UslugaController {

    private final UslugaRepository uslugaRepository;

    public UslugaController(UslugaRepository uslugaRepository) {
        this.uslugaRepository = uslugaRepository;
    }

    @GetMapping
    public ResponseEntity<List<UslugaDTO>> getAll() {
        List<UslugaDTO> uslugi = uslugaRepository.findAll().stream().map(u -> {
            UslugaDTO dto = new UslugaDTO();
            dto.setId(u.getId());
            dto.setNazwa(u.getNazwa());
            dto.setOpis(u.getOpis());
            dto.setCzasTrwaniaMin(u.getCzasTrwaniaMin());
            dto.setCenaNetto(u.getCenaNetto());
            dto.setStawkaVat(u.getStawkavat());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(uslugi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UslugaDTO> getById(@PathVariable Long id) {
        return uslugaRepository.findById(id).map(u -> {
            UslugaDTO dto = new UslugaDTO();
            dto.setId(u.getId());
            dto.setNazwa(u.getNazwa());
            dto.setOpis(u.getOpis());
            dto.setCzasTrwaniaMin(u.getCzasTrwaniaMin());
            dto.setCenaNetto(u.getCenaNetto());
            dto.setStawkaVat(u.getStawkavat());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UslugaDTO> create(@RequestBody UslugaDTO dto) {
        Usluga usluga = new Usluga();
        usluga.setNazwa(dto.getNazwa());
        usluga.setOpis(dto.getOpis());
        usluga.setCzasTrwaniaMin(dto.getCzasTrwaniaMin());
        usluga.setCenaNetto(dto.getCenaNetto());
        usluga.setStawkavat(dto.getStawkaVat());

        usluga = uslugaRepository.save(usluga);
        dto.setId(usluga.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UslugaDTO> update(@PathVariable Long id, @RequestBody UslugaDTO dto) {
        return uslugaRepository.findById(id).map(usluga -> {
            usluga.setNazwa(dto.getNazwa());
            usluga.setOpis(dto.getOpis());
            usluga.setCzasTrwaniaMin(dto.getCzasTrwaniaMin());
            usluga.setCenaNetto(dto.getCenaNetto());
            usluga.setStawkavat(dto.getStawkaVat());
            
            uslugaRepository.save(usluga);
            dto.setId(usluga.getId());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (uslugaRepository.existsById(id)) {
            uslugaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
