package pl.fryzjer.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.fryzjer.entity.Uzytkownik;
import pl.fryzjer.repository.UzytkownikRepository;

// Serwis inicjalizujący domyślne dane systemowe przy starcie
// - tworzenie domyślnych kont użytkowników (admin, pracownik, klient)
// - tworzenie przykładowych usług i fryzjerów

@Service
public class AdminInitService {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitService(UzytkownikRepository uzytkownikRepository, PasswordEncoder passwordEncoder) {
        this.uzytkownikRepository = uzytkownikRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initAdminAccount() {
        if (uzytkownikRepository.findByUsername("admin").isEmpty()) {
            Uzytkownik admin = new Uzytkownik();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRola("ADMINISTRATOR");
            admin.setEmail("admin@punktciecia.pl");
            admin.setTelefon("000000000");
            
            uzytkownikRepository.save(admin);
            System.out.println(">>> Utworzono domyślne konto administratora: admin / admin123");
        } else {
            // Upewnij się, że stary admin ma zaktualizowaną rolę i pola, jeśli ich brakuje
            Uzytkownik admin = uzytkownikRepository.findByUsername("admin").get();
            boolean updated = false;
            
            if ("ADMIN".equals(admin.getRola())) {
                admin.setRola("ADMINISTRATOR");
                updated = true;
            }
            if (admin.getEmail() == null) {
                admin.setEmail("admin@punktciecia.pl");
                updated = true;
            }
            if (admin.getTelefon() == null) {
                admin.setTelefon("000000000");
                updated = true;
            }
            
            if (updated) {
                uzytkownikRepository.save(admin);
                System.out.println(">>> Zaktualizowano istniejące konto administratora.");
            }
        }
    }
}
