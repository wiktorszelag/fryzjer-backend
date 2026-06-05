package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.KlientDTO;
import pl.fryzjer.entity.Klient;
import pl.fryzjer.repository.KlientRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Kontroler do zarządzania profilami klientów
// - pobieranie listy klientów w salonie (tylko pracownicy/admin)
// - pobieranie profilu aktualnie zalogowanego klienta
// - tworzenie i edycja danych klienta (własnych)

@RestController
@RequestMapping("/api/klienci")
public class KlientController {

    private final KlientRepository klientRepository;

    public KlientController(KlientRepository klientRepository) {
        this.klientRepository = klientRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_ADMINISTRATOR") && !role.equals("ADMIN") && !role.equals("ADMINISTRATOR") && !role.equals("ROLE_PRACOWNIK") && !role.equals("PRACOWNIK")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak uprawnień do przeglądania listy klientów.");
        }

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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody KlientDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        Optional<Klient> existingKlient = klientRepository.findById(id);
        if (existingKlient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (role.equals("ROLE_KLIENT") || role.equals("KLIENT")) {
            if (existingKlient.get().getUsername() == null || !existingKlient.get().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Możesz edytować tylko własny profil.");
            }
        }

        Klient klient = existingKlient.get();
        klient.setImie(dto.getImie());
        klient.setNazwisko(dto.getNazwisko());
        klient.setTelefon(dto.getTelefon());
        klientRepository.save(klient);
        dto.setId(klient.getId());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_ADMINISTRATOR") && !role.equals("ADMIN") && !role.equals("ADMINISTRATOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tylko administrator może usuwać profile klientów.");
        }

        if (klientRepository.existsById(id)) {
            klientRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    public ResponseEntity<KlientDTO> getMe(org.springframework.security.core.Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        return klientRepository.findByUsername(username).map(k -> {
            KlientDTO dto = new KlientDTO();
            dto.setId(k.getId());
            dto.setImie(k.getImie());
            dto.setNazwisko(k.getNazwisko());
            dto.setTelefon(k.getTelefon());
            dto.setDataRejestracji(k.getDataRejestracji() != null ? k.getDataRejestracji().atStartOfDay() : null);
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> {
            Klient k = new Klient();
            k.setImie(username);
            k.setNazwisko("");
            k.setUsername(username);
            k.setDataRejestracji(java.time.LocalDate.now());
            k = klientRepository.save(k);
            
            KlientDTO dto = new KlientDTO();
            dto.setId(k.getId());
            dto.setImie(k.getImie());
            dto.setNazwisko(k.getNazwisko());
            dto.setTelefon(k.getTelefon());
            dto.setDataRejestracji(k.getDataRejestracji().atStartOfDay());
            return ResponseEntity.ok(dto);
        });
    }
}
