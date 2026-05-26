package pl.fryzjer.service;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Przechowuje pliki w pamięci RAM (demonstrator technologii).
 * Klucz mapy = oryginalna nazwa pliku.
 */

// Serwis do zapisywania i odczytywania plików certyfikatów z dysku
// - odczyt kluczy prywatnych i publicznych
// - zapis certyfikatów X.509

@Component
public class FileStore {

    // Przechowuje bajty pliku
    private final Map<String, byte[]> files = new ConcurrentHashMap<>();

    // Przechowuje podpis cyfrowy powiązany z plikiem
    private final Map<String, String> signatures = new ConcurrentHashMap<>();

    // Przechowuje typ MIME pliku
    private final Map<String, String> mimeTypes = new ConcurrentHashMap<>();

    public void store(String filename, byte[] data, String signature, String mimeType) {
        files.put(filename, data);
        signatures.put(filename, signature);
        mimeTypes.put(filename, mimeType != null ? mimeType : "application/octet-stream");
    }

    public byte[] getFile(String filename) {
        return files.get(filename);
    }

    public String getSignature(String filename) {
        return signatures.get(filename);
    }

    public String getMimeType(String filename) {
        return mimeTypes.getOrDefault(filename, "application/octet-stream");
    }

    public boolean exists(String filename) {
        return files.containsKey(filename);
    }

    public Set<String> getAllFilenames() {
        return files.keySet();
    }

    public long getFileSize(String filename) {
        byte[] data = files.get(filename);
        return data != null ? data.length : 0;
    }
}
