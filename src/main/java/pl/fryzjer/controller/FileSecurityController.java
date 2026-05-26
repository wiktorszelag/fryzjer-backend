package pl.fryzjer.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.fryzjer.service.CryptoDocumentService;
import pl.fryzjer.service.FileStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kontroler do demonstracji mechanizmu bezpieczeństwa plików.
 *
 * Przepływ:
 * 1. POST /api/files/upload      → wgraj plik, otrzymaj klucz (podpis cyfrowy)
 * 2. GET  /api/files/list        → lista plików na serwerze
 * 3. POST /api/files/download    → podaj nazwę pliku + klucz → pobierz plik
 * 4. POST /api/files/verify      → samo sprawdzenie klucza bez pobierania
 *
 * Klucz jest powiązany zarówno z zawartością pliku, jak i z jego nazwą —
 * zmiana nazwy lub treści unieważnia klucz.
 */

// Kontroler bezpieczeństwa dokumentów i podpisów cyfrowych
// - podpisywanie plików certyfikatem cyfrowym
// - weryfikacja podpisu cyfrowego plików
// - walidacja certyfikatów i kluczy

@RestController
@RequestMapping("/api/files")
public class FileSecurityController {

    private final CryptoDocumentService cryptoService;
    private final FileStore fileStore;

    public FileSecurityController(CryptoDocumentService cryptoService, FileStore fileStore) {
        this.cryptoService = cryptoService;
        this.fileStore = fileStore;
    }

    // ── Wgraj i podpisz plik ──────────────────────────────────────────────────

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Plik jest pusty."));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Brak nazwy pliku."));
        }

        try {
            byte[] fileBytes = file.getBytes();
            // Podpisujemy: bajty nazwy + bajty pliku — klucz jest powiązany z OBOMA
            byte[] dataToSign = buildSignaturePayload(filename, fileBytes);
            String key = cryptoService.signBytes(dataToSign);
            String mimeType = file.getContentType();

            fileStore.store(filename, fileBytes, key, mimeType);

            return ResponseEntity.ok(Map.of(
                "filename", filename,
                "size",     fileBytes.length,
                "key",      key,
                "message",  "Plik został wgrany i podpisany cyfrowo."
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Błąd odczytu pliku: " + e.getMessage()));
        }
    }

    // ── Lista plików ──────────────────────────────────────────────────────────

    @GetMapping("/list")
    public ResponseEntity<?> listFiles() {
        List<Map<String, Object>> result = fileStore.getAllFilenames().stream()
            .map(name -> {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("filename", name);
                info.put("size",     fileStore.getFileSize(name));
                return info;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ── Pobierz plik (wymaga klucza) ──────────────────────────────────────────

    @PostMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestBody Map<String, String> request) {
        String filename = request.get("filename");
        String key      = request.get("key");

        if (filename == null || key == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Wymagane pola: filename, key."));
        }

        if (!fileStore.exists(filename)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Plik nie istnieje na serwerze."));
        }

        byte[] fileBytes     = fileStore.getFile(filename);
        byte[] dataToVerify  = buildSignaturePayload(filename, fileBytes);
        boolean valid        = cryptoService.verifyBytes(dataToVerify, key);

        if (!valid) {
            return ResponseEntity.status(403)
                    .body(Map.of(
                        "valid",   false,
                        "error",   "Dostęp odmówiony: nieprawidłowy klucz lub plik/nazwa pliku zostały zmodyfikowane."
                    ));
        }

        String mimeType = fileStore.getMimeType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(mimeType))
                .body(fileBytes);
    }

    // ── Tylko weryfikacja (bez pobierania) ────────────────────────────────────

    @PostMapping("/verify")
    public ResponseEntity<?> verifyFile(@RequestBody Map<String, String> request) {
        String filename = request.get("filename");
        String key      = request.get("key");

        if (filename == null || key == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Wymagane pola: filename, key."));
        }

        if (!fileStore.exists(filename)) {
            return ResponseEntity.status(404)
                    .body(Map.of("valid", false, "message", "Plik nie istnieje na serwerze."));
        }

        byte[] fileBytes    = fileStore.getFile(filename);
        byte[] dataToVerify = buildSignaturePayload(filename, fileBytes);
        boolean valid       = cryptoService.verifyBytes(dataToVerify, key);

        String message = valid
            ? "Klucz poprawny — plik autentyczny i nienaruszony."
            : "Nieprawidłowy klucz: plik lub jego nazwa zostały zmienione, albo klucz pochodzi z innego źródła.";

        return ResponseEntity.ok(Map.of("valid", valid, "message", message));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Łączy bajty nazwy pliku i bajty zawartości w jeden bufor do podpisania.
     * Dzięki temu zmiana nazwy LUB zawartości pliku unieważnia klucz.
     */
    private byte[] buildSignaturePayload(String filename, byte[] fileBytes) {
        byte[] nameBytes = filename.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + nameBytes.length + fileBytes.length);
        buf.putInt(nameBytes.length);
        buf.put(nameBytes);
        buf.put(fileBytes);
        return buf.array();
    }
}
