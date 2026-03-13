package pl.fryzjer.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.fryzjer.security.entity.Rola;
import pl.fryzjer.security.entity.Uzytkownik;
import pl.fryzjer.security.repository.UzytkownikRepository;

@Service
@RequiredArgsConstructor
public class AdminInitService {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tworzy domyślne konto administratora przy starcie aplikacji,
     * jeśli jeszcze nie istnieje. Login: admin, Hasło: admin123
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initAdminAccount() {
        if (uzytkownikRepository.findByUsername("admin").isEmpty()) {
            Uzytkownik admin = Uzytkownik.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .aktywny(true)
                    .rola(Rola.ADMINISTRATOR)
                    .build();
            uzytkownikRepository.save(admin);
            System.out.println(">>> Utworzono domyślne konto administratora: admin / admin123");
        }
    }
}
