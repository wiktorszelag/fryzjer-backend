package pl.fryzjer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fryzjer.dto.FryzjerUslugaDTO;
import pl.fryzjer.entity.Fryzjer;
import pl.fryzjer.entity.FryzjerUsluga;
import pl.fryzjer.entity.Usluga;
import pl.fryzjer.repository.FryzjerRepository;
import pl.fryzjer.repository.FryzjerUslugaRepository;
import pl.fryzjer.repository.UslugaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FryzjerUslugaService {

    private final FryzjerUslugaRepository fryzjerUslugaRepository;
    private final FryzjerRepository fryzjerRepository;
    private final UslugaRepository uslugaRepository;

    public List<FryzjerUslugaDTO> findAll() {
        return fryzjerUslugaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FryzjerUslugaDTO findById(Integer id) {
        return fryzjerUslugaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Relacja fryzjer-usługa nie znaleziona"));
    }

    public FryzjerUslugaDTO create(FryzjerUslugaDTO dto) {
        Fryzjer fryzjer = fryzjerRepository.findById(dto.getFryzjerId())
                .orElseThrow(() -> new RuntimeException("Fryzjer nie znaleziony"));
        Usluga usluga = uslugaRepository.findById(dto.getUslugaId())
                .orElseThrow(() -> new RuntimeException("Usługa nie znaleziona"));

        FryzjerUsluga fryzjerUsluga = FryzjerUsluga.builder()
                .fryzjer(fryzjer)
                .usluga(usluga)
                .cenaFryzjera(dto.getCenaFryzjera())
                .build();

        return toDTO(fryzjerUslugaRepository.save(fryzjerUsluga));
    }

    public FryzjerUslugaDTO update(Integer id, FryzjerUslugaDTO dto) {
        FryzjerUsluga fryzjerUsluga = fryzjerUslugaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relacja fryzjer-usługa nie znaleziona"));
        fryzjerUsluga.setCenaFryzjera(dto.getCenaFryzjera());
        return toDTO(fryzjerUslugaRepository.save(fryzjerUsluga));
    }

    public void delete(Integer id) {
        fryzjerUslugaRepository.deleteById(id);
    }

    public List<FryzjerUslugaDTO> findByFryzjerId(Integer fryzjerId) {
        return fryzjerUslugaRepository.findByFryzjerFryzjerId(fryzjerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<FryzjerUslugaDTO> findByUslugaId(Integer uslugaId) {
        return fryzjerUslugaRepository.findByUslugaUslugaId(uslugaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private FryzjerUslugaDTO toDTO(FryzjerUsluga fryzjerUsluga) {
        return FryzjerUslugaDTO.builder()
                .fryzjerUslugaId(fryzjerUsluga.getFryzjerUslugaId())
                .fryzjerId(fryzjerUsluga.getFryzjer().getFryzjerId())
                .uslugaId(fryzjerUsluga.getUsluga().getUslugaId())
                .cenaFryzjera(fryzjerUsluga.getCenaFryzjera())
                .build();
    }
}
