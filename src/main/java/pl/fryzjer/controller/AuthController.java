package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.AuthResponse;
import pl.fryzjer.dto.LoginRequest;
import pl.fryzjer.dto.RegisterRequest;
import pl.fryzjer.entity.Uzytkownik;
import pl.fryzjer.repository.UzytkownikRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.fryzjer.config.JwtUtil;

import java.util.Optional;

import pl.fryzjer.entity.Klient;
import pl.fryzjer.repository.KlientRepository;
import java.time.LocalDate;

// Kontroler uwierzytelniania i rejestracji
// - logowanie użytkowników i weryfikacja haseł
// - rejestracja kont użytkowników o roli KLIENT
// - automatyczne tworzenie profilu klienta dla zarejestrowanego użytkownika
// - pobieranie danych o zalogowanym użytkowniku

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UzytkownikRepository uzytkownikRepository;
    private final KlientRepository klientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UzytkownikRepository uzytkownikRepository, KlientRepository klientRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.uzytkownikRepository = uzytkownikRepository;
        this.klientRepository = klientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (uzytkownikRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(request.getUsername(), request.getRola(), "Użytkownik już istnieje.", null));
        }

        Uzytkownik uzytkownik = new Uzytkownik();
        uzytkownik.setUsername(request.getUsername());
        uzytkownik.setPassword(passwordEncoder.encode(request.getPassword()));
        uzytkownik.setRola(request.getRola() != null ? request.getRola() : "KLIENT");
        uzytkownik.setEmail(request.getEmail());
        uzytkownik.setTelefon(request.getTelefon());
        
        uzytkownikRepository.save(uzytkownik);

        if ("KLIENT".equals(uzytkownik.getRola())) {
            Klient klient = new Klient();
            klient.setImie(uzytkownik.getUsername());
            klient.setNazwisko("");
            klient.setTelefon(uzytkownik.getTelefon());
            klient.setUsername(uzytkownik.getUsername());
            klient.setDataRejestracji(LocalDate.now());
            klientRepository.save(klient);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(uzytkownik.getUsername(), uzytkownik.getRola(), "Zarejestrowano pomyślnie.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<Uzytkownik> userOpt = uzytkownikRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            Uzytkownik uzytkownik = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), uzytkownik.getPassword())) {
                String token = jwtUtil.generateToken(uzytkownik.getUsername(), uzytkownik.getRola());
                return ResponseEntity.ok(new AuthResponse(uzytkownik.getUsername(), uzytkownik.getRola(), "Zalogowano pomyślnie.", token));
            } else if (!uzytkownik.getPassword().startsWith("$2a$") && uzytkownik.getPassword().equals(request.getPassword())) {
                // Automatyczna migracja starego hasła (plaintext) na BCrypt
                uzytkownik.setPassword(passwordEncoder.encode(request.getPassword()));
                uzytkownikRepository.save(uzytkownik);
                String token = jwtUtil.generateToken(uzytkownik.getUsername(), uzytkownik.getRola());
                return ResponseEntity.ok(new AuthResponse(uzytkownik.getUsername(), uzytkownik.getRola(), "Zalogowano pomyślnie (hasło zmigrowane).", token));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("KLIENT");
            return ResponseEntity.ok(new AuthResponse(username, role, "Pobrano dane.", null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
