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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UzytkownikRepository uzytkownikRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.uzytkownikRepository = uzytkownikRepository;
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
        
        uzytkownikRepository.save(uzytkownik);

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
