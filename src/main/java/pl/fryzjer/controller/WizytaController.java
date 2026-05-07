package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.WizytaDTO;
import pl.fryzjer.entity.Wizyta;
import pl.fryzjer.repository.WizytaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wizyty")
public class WizytaController {

    private final WizytaRepository wizytaRepository;

    public WizytaController(WizytaRepository wizytaRepository) {
        this.wizytaRepository = wizytaRepository;
    }

    private WizytaDTO mapToDto(Wizyta w) {
        WizytaDTO dto = new WizytaDTO();
        dto.setId(w.getId());
        dto.setKlientId(w.getKlientId());
        dto.setFryzjerId(w.getFryzjerId());
        dto.setDataGodzinaRozpoczecia(w.getDataGodzinaRozpoczecia());
        dto.setCzasTrwaniaCalkowity(w.getCzasTrwaniaCalkowity());
        // Upraszczamy zwrot same j wizyty dla labów
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<WizytaDTO>> getAll() {
        List<WizytaDTO> wizyty = wizytaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wizyty);
    }

    @PostMapping
    public ResponseEntity<WizytaDTO> create(@RequestBody WizytaDTO dto) {
        Wizyta wizyta = new Wizyta();
        wizyta.setKlientId(dto.getKlientId());
        wizyta.setFryzjerId(dto.getFryzjerId());
        wizyta.setDataGodzinaRozpoczecia(dto.getDataGodzinaRozpoczecia());
        wizyta.setCzasTrwaniaCalkowity(dto.getCzasTrwaniaCalkowity());
        wizyta.setDataRezerwacji(LocalDate.now());

        wizyta = wizytaRepository.save(wizyta);
        dto.setId(wizyta.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WizytaDTO> update(@PathVariable Long id, @RequestBody WizytaDTO dto) {
        return wizytaRepository.findById(id).map(wizyta -> {
            wizyta.setKlientId(dto.getKlientId());
            wizyta.setFryzjerId(dto.getFryzjerId());
            wizyta.setDataGodzinaRozpoczecia(dto.getDataGodzinaRozpoczecia());
            wizyta.setCzasTrwaniaCalkowity(dto.getCzasTrwaniaCalkowity());
            
            wizytaRepository.save(wizyta);
            dto.setId(wizyta.getId());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (wizytaRepository.existsById(id)) {
            wizytaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/klient/{klientId}")
    public ResponseEntity<List<WizytaDTO>> getByKlient(@PathVariable Long klientId) {
        List<WizytaDTO> wizyty = wizytaRepository.findByKlientId(klientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wizyty);
    }

    @GetMapping("/fryzjer/{fryzjerId}")
    public ResponseEntity<List<WizytaDTO>> getByFryzjer(@PathVariable Long fryzjerId) {
        List<WizytaDTO> wizyty = wizytaRepository.findByFryzjerId(fryzjerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wizyty);
    }
}
