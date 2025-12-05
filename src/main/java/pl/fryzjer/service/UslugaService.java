package pl.fryzjer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fryzjer.dto.UslugaDTO;
import pl.fryzjer.entity.Usluga;
import pl.fryzjer.repository.UslugaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UslugaService {

    private final UslugaRepository uslugaRepository;

    public List<UslugaDTO> findAll() {
        return uslugaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UslugaDTO findById(Integer id) {
        return uslugaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usługa nie znaleziona"));
    }

    public UslugaDTO create(UslugaDTO dto) {
        Usluga usluga = toEntity(dto);
        return toDTO(uslugaRepository.save(usluga));
    }

    public UslugaDTO update(Integer id, UslugaDTO dto) {
        Usluga usluga = uslugaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usługa nie znaleziona"));
        usluga.setNazwa(dto.getNazwa());
        usluga.setOpis(dto.getOpis());
        usluga.setCzasTrwaniaMin(dto.getCzasTrwaniaMin());
        usluga.setCenaNetto(dto.getCenaNetto());
        usluga.setStawkaVat(dto.getStawkaVat());
        return toDTO(uslugaRepository.save(usluga));
    }

    public void delete(Integer id) {
        uslugaRepository.deleteById(id);
    }

    private UslugaDTO toDTO(Usluga usluga) {
        return UslugaDTO.builder()
                .uslugaId(usluga.getUslugaId())
                .nazwa(usluga.getNazwa())
                .opis(usluga.getOpis())
                .czasTrwaniaMin(usluga.getCzasTrwaniaMin())
                .cenaNetto(usluga.getCenaNetto())
                .stawkaVat(usluga.getStawkaVat())
                .build();
    }

    private Usluga toEntity(UslugaDTO dto) {
        return Usluga.builder()
                .nazwa(dto.getNazwa())
                .opis(dto.getOpis())
                .czasTrwaniaMin(dto.getCzasTrwaniaMin())
                .cenaNetto(dto.getCenaNetto())
                .stawkaVat(dto.getStawkaVat())
                .build();
    }
}
