package pl.fryzjer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fryzjer.dto.FryzjerDTO;
import pl.fryzjer.entity.Fryzjer;
import pl.fryzjer.repository.FryzjerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FryzjerService {

    private final FryzjerRepository fryzjerRepository;

    public List<FryzjerDTO> findAll() {
        return fryzjerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FryzjerDTO findById(Integer id) {
        return fryzjerRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Fryzjer nie znaleziony"));
    }

    public FryzjerDTO create(FryzjerDTO dto) {
        Fryzjer fryzjer = toEntity(dto);
        return toDTO(fryzjerRepository.save(fryzjer));
    }

    public FryzjerDTO update(Integer id, FryzjerDTO dto) {
        Fryzjer fryzjer = fryzjerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fryzjer nie znaleziony"));
        fryzjer.setImie(dto.getImie());
        fryzjer.setNazwisko(dto.getNazwisko());
        fryzjer.setTelefon(dto.getTelefon());
        fryzjer.setSpecjalizacja(dto.getSpecjalizacja());
        fryzjer.setDataZatrudnienia(dto.getDataZatrudnienia());
        return toDTO(fryzjerRepository.save(fryzjer));
    }

    public void delete(Integer id) {
        fryzjerRepository.deleteById(id);
    }

    public List<FryzjerDTO> findBySpecjalizacja(String specjalizacja) {
        return fryzjerRepository.findBySpecjalizacja(specjalizacja).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private FryzjerDTO toDTO(Fryzjer fryzjer) {
        return FryzjerDTO.builder()
                .fryzjerId(fryzjer.getFryzjerId())
                .imie(fryzjer.getImie())
                .nazwisko(fryzjer.getNazwisko())
                .telefon(fryzjer.getTelefon())
                .specjalizacja(fryzjer.getSpecjalizacja())
                .dataZatrudnienia(fryzjer.getDataZatrudnienia())
                .build();
    }

    private Fryzjer toEntity(FryzjerDTO dto) {
        return Fryzjer.builder()
                .imie(dto.getImie())
                .nazwisko(dto.getNazwisko())
                .telefon(dto.getTelefon())
                .specjalizacja(dto.getSpecjalizacja())
                .dataZatrudnienia(dto.getDataZatrudnienia())
                .build();
    }
}
