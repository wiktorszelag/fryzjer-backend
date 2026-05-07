package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.KlientDTO;
import pl.fryzjer.entity.Klient;
import pl.fryzjer.repository.KlientRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/klienci")
public class KlientController {

    private final KlientRepository klientRepository;

    public KlientController(KlientRepository klientRepository) {
        this.klientRepository = klientRepository;
    }

    @GetMapping
    public ResponseEntity<List<KlientDTO>> getAll() {
        List<KlientDTO> klienci = klientRepository.findAll().stream().map(k -> {
            KlientDTO dto = new KlientDTO();
            dto.setId(k.getId());
            dto.setImie(k.getImie());
            dto.setNazwisko(k.getNazwisko());
            dto.setTelefon(k.getTelefon());
            dto.setDataRejestracji(k.getDataRejestracji() != null ? k.getDataRejestracji().atStartOfDay() : null);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(klienci);
    }

    @PostMapping
    public ResponseEntity<KlientDTO> create(@RequestBody KlientDTO dto) {
        Klient klient = new Klient();
        klient.setImie(dto.getImie());
        klient.setNazwisko(dto.getNazwisko());
        klient.setTelefon(dto.getTelefon());
        klient.setDataRejestracji(java.time.LocalDate.now());

        klient = klientRepository.save(klient);
        dto.setId(klient.getId());
        dto.setDataRejestracji(klient.getDataRejestracji().atStartOfDay());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KlientDTO> update(@PathVariable Long id, @RequestBody KlientDTO dto) {
        return klientRepository.findById(id).map(klient -> {
            klient.setImie(dto.getImie());
            klient.setNazwisko(dto.getNazwisko());
            klient.setTelefon(dto.getTelefon());
            klientRepository.save(klient);
            dto.setId(klient.getId());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (klientRepository.existsById(id)) {
            klientRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
