package pl.fryzjer.service;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Service
public class CryptoDocumentService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public CryptoDocumentService() {
        try {
            // W celach demonstracyjnych generujemy parę kluczy RSA (2048 bitów) przy starcie aplikacji
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Błąd podczas inicjalizacji kluczy RSA", e);
        }
    }

    /**
     * Podpisuje dokument (tekst). 
     * Wylicza skrót SHA-256 z podanego tekstu, a następnie szyfruje go Kluczem Prywatnym RSA.
     */
    public String signDocument(String documentContent) {
        try {
            // 1. Inicjalizacja algorytmu podpisu (SHA256withRSA wylicza hash SHA-256 i od razu szyfruje go RSA)
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            
            // 2. Przekazanie danych dokumentu do podpisania
            byte[] dataBytes = documentContent.getBytes(StandardCharsets.UTF_8);
            signature.update(dataBytes);
            
            // 3. Generowanie podpisu cyfrowego
            byte[] digitalSignatureBytes = signature.sign();
            
            // Zwracamy jako czytelny łańcuch znaków Base64
            return Base64.getEncoder().encodeToString(digitalSignatureBytes);
            
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się podpisać dokumentu", e);
        }
    }

    /**
     * Weryfikuje integralność i autentyczność dokumentu.
     * Wylicza hash SHA-256 z tekstu i porównuje go z hashem odzyskanym z Podpisu (za pomocą Klucza Publicznego).
     */
    public boolean verifyDocument(String documentContent, String base64Signature) {
        try {
            // 1. Dekodowanie podpisu z Base64 do bajtów
            byte[] digitalSignatureBytes = Base64.getDecoder().decode(base64Signature);

            // 2. Inicjalizacja algorytmu do weryfikacji (wymaga Klucza Publicznego)
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);

            // 3. Przekazanie sprawdzanego dokumentu
            byte[] dataBytes = documentContent.getBytes(StandardCharsets.UTF_8);
            signature.update(dataBytes);

            // 4. Weryfikacja: algorytm sam odkoduje hash z podpisu i porówna z nowo wyliczonym hashem
            return signature.verify(digitalSignatureBytes);
            
        } catch (Exception e) {
            // Jakikolwiek błąd (zły klucz, zepsuty format Base64) traktujemy jako nieudaną weryfikację
            return false;
        }
    }
}
