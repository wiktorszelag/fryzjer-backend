package pl.fryzjer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fryzjer.dto.KlientDTO;
import pl.fryzjer.entity.Klient;
import pl.fryzjer.repository.KlientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KlientService {

    private final KlientRepository klientRepository;

    public List<KlientDTO> findAll() {
        return klientRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public KlientDTO findById(Integer id) {
        return klientRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));
    }

    public KlientDTO create(KlientDTO dto) {
        Klient klient = toEntity(dto);
        klient.setDataRejestracji(LocalDate.now());
        return toDTO(klientRepository.save(klient));
    }

    public KlientDTO update(Integer id, KlientDTO dto) {
        Klient klient = klientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));
        klient.setImie(dto.getImie());
        klient.setNazwisko(dto.getNazwisko());
        klient.setTelefon(dto.getTelefon());
        return toDTO(klientRepository.save(klient));
    }

    public void delete(Integer id) {
        klientRepository.deleteById(id);
    }

    private KlientDTO toDTO(Klient klient) {
        return KlientDTO.builder()
                .klientId(klient.getKlientId())
                .imie(klient.getImie())
                .nazwisko(klient.getNazwisko())
                .telefon(klient.getTelefon())
                .dataRejestracji(klient.getDataRejestracji())
                .build();
    }

    private Klient toEntity(KlientDTO dto) {
        return Klient.builder()
                .imie(dto.getImie())
                .nazwisko(dto.getNazwisko())
                .telefon(dto.getTelefon())
                .build();
    }
}
