package pl.fryzjer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.RoleUpdateRequest;
import pl.fryzjer.entity.Fryzjer;
import pl.fryzjer.entity.Uzytkownik;
import pl.fryzjer.repository.FryzjerRepository;
import pl.fryzjer.repository.UzytkownikRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UzytkownikRepository uzytkownikRepository;
    private final FryzjerRepository fryzjerRepository;

    public AdminController(UzytkownikRepository uzytkownikRepository, FryzjerRepository fryzjerRepository) {
        this.uzytkownikRepository = uzytkownikRepository;
        this.fryzjerRepository = fryzjerRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Uzytkownik>> getAllUsers() {
        return ResponseEntity.ok(uzytkownikRepository.findAll());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        Optional<Uzytkownik> userOpt = uzytkownikRepository.findById(id);
        if (userOpt.isPresent()) {
            Uzytkownik user = userOpt.get();
            user.setRola(request.getRola());
            uzytkownikRepository.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/fryzjerzy")
    public ResponseEntity<List<Fryzjer>> getAllFryzjerzy() {
        return ResponseEntity.ok(fryzjerRepository.findAll());
    }

    @PostMapping("/fryzjerzy")
    public ResponseEntity<Fryzjer> addFryzjer(@RequestBody Fryzjer fryzjer) {
        if (fryzjer.getDataZatrudnienia() == null) {
            fryzjer.setDataZatrudnienia(LocalDate.now());
        }
        Fryzjer saved = fryzjerRepository.save(fryzjer);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (uzytkownikRepository.existsById(id)) {
            uzytkownikRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/fryzjerzy/{id}/specjalizacja")
    public ResponseEntity<?> updateFryzjerSpecjalizacja(@PathVariable Long id, @RequestBody Fryzjer updateRequest) {
        Optional<Fryzjer> fOpt = fryzjerRepository.findById(id);
        if(fOpt.isPresent()) {
            Fryzjer f = fOpt.get();
            f.setSpecjalizacja(updateRequest.getSpecjalizacja());
            fryzjerRepository.save(f);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
