package pl.fryzjer.security.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.security.dto.LoginRequest;
import pl.fryzjer.security.dto.LoginResponse;
import pl.fryzjer.security.dto.RegisterRequest;
import pl.fryzjer.security.entity.Rola;
import pl.fryzjer.security.entity.Uzytkownik;
import pl.fryzjer.security.repository.UzytkownikRepository;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowCredentials = "true")
public class AuthController {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<Uzytkownik> optUser = uzytkownikRepository.findByUsername(request.username());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("komunikat", "Nieprawidłowa nazwa użytkownika lub hasło."));
        }
        Uzytkownik user = optUser.get();
        if (!Boolean.TRUE.equals(user.getAktywny())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("komunikat", "Konto jest nieaktywne."));
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("komunikat", "Nieprawidłowa nazwa użytkownika lub hasło."));
        }

        session.setAttribute("username", user.getUsername());
        session.setAttribute("rola", user.getRola().name());

        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRola().name(), "Zalogowano pomyślnie."));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.username() == null || request.username().trim().length() < 3) {
            return ResponseEntity.badRequest()
                    .body(Map.of("komunikat", "Nazwa użytkownika musi mieć co najmniej 3 znaki."));
        }
        if (request.password() == null || request.password().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("komunikat", "Hasło musi mieć co najmniej 6 znaków."));
        }
        if (uzytkownikRepository.findByUsername(request.username().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("komunikat", "Użytkownik o tej nazwie już istnieje."));
        }

        // Określ rolę – domyślnie KLIENT, blokuj ADMINISTRATOR przez formularz
        Rola rola;
        String rolaStr = request.rola() != null ? request.rola().toUpperCase() : "KLIENT";

        if (rolaStr.equals("ADMINISTRATOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("komunikat", "Nie można zarejestrować konta administratora."));
        }

        // Weryfikacja kodu dostępu dla pracownika
        if (rolaStr.equals("PRACOWNIK")) {
            if (request.kodDostepu() == null || !request.kodDostepu().equals("praca")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("komunikat", "Nieprawidłowy kod dostępu pracownika."));
            }
            rola = Rola.PRACOWNIK;
        } else {
            rola = Rola.KLIENT;
        }

        Uzytkownik nowyUzytkownik = Uzytkownik.builder()
                .username(request.username().trim())
                .password(passwordEncoder.encode(request.password()))
                .aktywny(true)
                .rola(rola)
                .build();
        uzytkownikRepository.save(nowyUzytkownik);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(nowyUzytkownik.getUsername(), nowyUzytkownik.getRola().name(), "Konto zostało utworzone pomyślnie."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("komunikat", "Wylogowano pomyślnie."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String rola = (String) session.getAttribute("rola");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("komunikat", "Nie jesteś zalogowany."));
        }
        return ResponseEntity.ok(new LoginResponse(username, rola, "Sesja aktywna."));
    }
}
