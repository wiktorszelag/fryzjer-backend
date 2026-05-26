package pl.fryzjer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Klasa główna uruchomieniowa aplikacji Spring Boot
// - start serwera aplikacji i uruchomienie kontekstu Spring

@SpringBootApplication
public class FryzjerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FryzjerApplication.class, args);
    }
}
