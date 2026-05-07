package pl.fryzjer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fryzjer.dto.SignRequest;
import pl.fryzjer.dto.SignResponse;
import pl.fryzjer.dto.VerifyRequest;
import pl.fryzjer.dto.VerifyResponse;
import pl.fryzjer.service.CryptoDocumentService;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Zabezpieczenia Dokumentów", description = "Endpointy do generowania i weryfikacji podpisów cyfrowych (SHA-256 + RSA)")
public class DocumentController {

    private final CryptoDocumentService cryptoService;

    public DocumentController(CryptoDocumentService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/sign")
    @Operation(summary = "Podpisz dokument (Zapewnij autentyczność)", description = "Generuje Hash SHA-256 z tekstu i szyfruje go Kluczem Prywatnym serwera.")
    public ResponseEntity<SignResponse> signDocument(@RequestBody SignRequest request) {
        String signature = cryptoService.signDocument(request.getDocumentContent());
        return ResponseEntity.ok(new SignResponse(signature));
    }

    @PostMapping("/verify")
    @Operation(summary = "Zweryfikuj dokument (Sprawdź integralność i autentyczność)", description = "Wylicza na nowo Hash z tekstu i porównuje go z Hashem odzyskanym z Podpisu za pomocą Klucza Publicznego.")
    public ResponseEntity<VerifyResponse> verifyDocument(@RequestBody VerifyRequest request) {
        boolean isValid = cryptoService.verifyDocument(request.getDocumentContent(), request.getSignature());
        
        String message = isValid 
            ? "Weryfikacja pomyślna: Dokument jest autentyczny i nienaruszony." 
            : "BŁĄD: Dokument został zmodyfikowany (naruszona integralność) lub pochodzi z nieautoryzowanego źródła!";
            
        return ResponseEntity.ok(new VerifyResponse(isValid, message));
    }
}
