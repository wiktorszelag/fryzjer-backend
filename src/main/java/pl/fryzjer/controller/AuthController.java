package pl.fryzjer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.AuthResponse;
import pl.fryzjer.dto.LoginRequest;
import pl.fryzjer.dto.RegisterRequest;
import pl.fryzjer.entity.Uzytkownik;
import pl.fryzjer.repository.UzytkownikRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UzytkownikRepository uzytkownikRepository;

    public AuthController(UzytkownikRepository uzytkownikRepository) {
        this.uzytkownikRepository = uzytkownikRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (uzytkownikRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(request.getUsername(), request.getRola(), "Użytkownik już istnieje."));
        }

        Uzytkownik uzytkownik = new Uzytkownik();
        uzytkownik.setUsername(request.getUsername());
        uzytkownik.setPassword(request.getPassword()); // Wymagane hashowanie w produkcji!
        uzytkownik.setRola(request.getRola() != null ? request.getRola() : "KLIENT");
        
        uzytkownikRepository.save(uzytkownik);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(uzytkownik.getUsername(), uzytkownik.getRola(), "Zarejestrowano pomyślnie."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<Uzytkownik> userOpt = uzytkownikRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            Uzytkownik uzytkownik = userOpt.get();
            // Porównanie plaintextowe hasła - tylko do celów demonstracyjnych/lab
            if (uzytkownik.getPassword().equals(request.getPassword())) {
                return ResponseEntity.ok(new AuthResponse(uzytkownik.getUsername(), uzytkownik.getRola(), "Zalogowano pomyślnie."));
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
        // Mockowanie pobrania użytkownika z kontekstu Spring Security
        return ResponseEntity.ok(new AuthResponse("aktualny_user", "KLIENT", "Pobrano dane."));
    }
}
