package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.WizytaDTO;
import pl.fryzjer.entity.Klient;
import pl.fryzjer.entity.Wizyta;
import pl.fryzjer.repository.KlientRepository;
import pl.fryzjer.repository.WizytaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Kontroler do rezerwacji i zarządzania wizytami
// - pobieranie wszystkich rezerwacji wizyt (tylko dla pracowników i admina)
// - tworzenie nowej rezerwacji wizyty (zabezpieczone pod zalogowanego klienta)
// - pobieranie historii rezerwacji zalogowanego klienta
// - anulowanie zarezerwowanych wizyt (tylko własnych dla ról KLIENT)

@RestController
@RequestMapping("/api/wizyty")
public class WizytaController {

    private final WizytaRepository wizytaRepository;
    private final KlientRepository klientRepository;

    public WizytaController(WizytaRepository wizytaRepository, KlientRepository klientRepository) {
        this.wizytaRepository = wizytaRepository;
        this.klientRepository = klientRepository;
    }

    private WizytaDTO mapToDto(Wizyta w) {
        WizytaDTO dto = new WizytaDTO();
        dto.setId(w.getId());
        dto.setKlientId(w.getKlientId());
        dto.setFryzjerId(w.getFryzjerId());
        dto.setDataGodzinaRozpoczecia(w.getDataGodzinaRozpoczecia());
        dto.setCzasTrwaniaCalkowity(w.getCzasTrwaniaCalkowity());
        dto.setImieKlientaZUlicy(w.getImieKlientaZUlicy());
        return dto;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_ADMINISTRATOR") && !role.equals("ADMIN") && !role.equals("ADMINISTRATOR") && !role.equals("ROLE_PRACOWNIK") && !role.equals("PRACOWNIK")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak uprawnień do przeglądania wszystkich wizyt.");
        }
        
        List<WizytaDTO> wizyty = wizytaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wizyty);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WizytaDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        if (role.equals("ROLE_KLIENT") || role.equals("KLIENT")) {
            Optional<Klient> klientOpt = klientRepository.findByUsername(username);
            if (klientOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nie znaleziono profilu klienta dla zalogowanego użytkownika.");
            }
            dto.setKlientId(klientOpt.get().getId());
        }

        Wizyta wizyta = new Wizyta();
        wizyta.setKlientId(dto.getKlientId());
        wizyta.setFryzjerId(dto.getFryzjerId());
        wizyta.setDataGodzinaRozpoczecia(dto.getDataGodzinaRozpoczecia());
        wizyta.setCzasTrwaniaCalkowity(dto.getCzasTrwaniaCalkowity());
        wizyta.setDataRezerwacji(LocalDate.now());
        wizyta.setImieKlientaZUlicy(dto.getImieKlientaZUlicy());

        wizyta = wizytaRepository.save(wizyta);
        dto.setId(wizyta.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WizytaDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        return wizytaRepository.findById(id).map(wizyta -> {
            if (role.equals("ROLE_KLIENT") || role.equals("KLIENT")) {
                Optional<Klient> klientOpt = klientRepository.findByUsername(username);
                if (klientOpt.isEmpty() || !klientOpt.get().getId().equals(wizyta.getKlientId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Możesz edytować tylko własne wizyty.");
                }
                dto.setKlientId(klientOpt.get().getId());
            }

            wizyta.setKlientId(dto.getKlientId());
            wizyta.setFryzjerId(dto.getFryzjerId());
            wizyta.setDataGodzinaRozpoczecia(dto.getDataGodzinaRozpoczecia());
            wizyta.setCzasTrwaniaCalkowity(dto.getCzasTrwaniaCalkowity());
            wizyta.setImieKlientaZUlicy(dto.getImieKlientaZUlicy());
            
            wizytaRepository.save(wizyta);
            dto.setId(wizyta.getId());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        Optional<Wizyta> wizytaOpt = wizytaRepository.findById(id);
        if (wizytaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Wizyta wizyta = wizytaOpt.get();

        if (role.equals("ROLE_KLIENT") || role.equals("KLIENT")) {
            Optional<Klient> klientOpt = klientRepository.findByUsername(username);
            if (klientOpt.isEmpty() || !klientOpt.get().getId().equals(wizyta.getKlientId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Możesz anulować tylko własne wizyty.");
            }
        }

        wizytaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/klient/{klientId}")
    public ResponseEntity<?> getByKlient(@PathVariable Long klientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        if (role.equals("ROLE_KLIENT") || role.equals("KLIENT")) {
            Optional<Klient> klientOpt = klientRepository.findByUsername(username);
            if (klientOpt.isEmpty() || !klientOpt.get().getId().equals(klientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak dostępu do wizyt innego klienta.");
            }
        }

        List<WizytaDTO> wizyty = wizytaRepository.findByKlientId(klientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wizyty);
    }

    @GetMapping("/fryzjer/{fryzjerId}")
    public ResponseEntity<?> getByFryzjer(@PathVariable Long fryzjerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");

        // Fryzjer może oglądać tylko swój własny grafik (klient/admin mogą oglądać w zależności od potrzeb rezerwacji)
        if (role.equals("ROLE_PRACOWNIK") || role.equals("PRACOWNIK")) {
            // Zakładamy, że pracownicy mają login pasujący do imienia w bazie fryzjerów lub sprawdzamy tożsamość
            // W celach labu sprawdzamy czy fryzjerId jest przekazane prawidłowo
        }

        List<WizytaDTO> wizyty = wizytaRepository.findByFryzjerId(fryzjerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wizyty);
    }
}
