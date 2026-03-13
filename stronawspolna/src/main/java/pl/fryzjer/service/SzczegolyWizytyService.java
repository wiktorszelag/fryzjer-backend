package pl.fryzjer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fryzjer.dto.SzczegolyWizytyDTO;
import pl.fryzjer.entity.SzczegolyWizyty;
import pl.fryzjer.entity.Usluga;
import pl.fryzjer.entity.Wizyta;
import pl.fryzjer.repository.SzczegolyWizytyRepository;
import pl.fryzjer.repository.UslugaRepository;
import pl.fryzjer.repository.WizytaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SzczegolyWizytyService {

    private final SzczegolyWizytyRepository szczegolyWizytyRepository;
    private final WizytaRepository wizytaRepository;
    private final UslugaRepository uslugaRepository;

    public List<SzczegolyWizytyDTO> findAll() {
        return szczegolyWizytyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SzczegolyWizytyDTO findById(Integer id) {
        return szczegolyWizytyRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Szczegóły wizyty nie znalezione"));
    }

    public SzczegolyWizytyDTO create(SzczegolyWizytyDTO dto) {
        Wizyta wizyta = wizytaRepository.findById(dto.getWizytaId())
                .orElseThrow(() -> new RuntimeException("Wizyta nie znaleziona"));
        Usluga usluga = uslugaRepository.findById(dto.getUslugaId())
                .orElseThrow(() -> new RuntimeException("Usługa nie znaleziona"));

        SzczegolyWizyty szczegoly = SzczegolyWizyty.builder()
                .wizyta(wizyta)
                .usluga(usluga)
                .cenaLacznaUslugi(dto.getCenaLacznaUslugi())
                .build();

        return toDTO(szczegolyWizytyRepository.save(szczegoly));
    }

    public SzczegolyWizytyDTO update(Integer id, SzczegolyWizytyDTO dto) {
        SzczegolyWizyty szczegoly = szczegolyWizytyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Szczegóły wizyty nie znalezione"));
        szczegoly.setCenaLacznaUslugi(dto.getCenaLacznaUslugi());
        return toDTO(szczegolyWizytyRepository.save(szczegoly));
    }

    public void delete(Integer id) {
        szczegolyWizytyRepository.deleteById(id);
    }

    public List<SzczegolyWizytyDTO> findByWizytaId(Integer wizytaId) {
        return szczegolyWizytyRepository.findByWizytaWizytaId(wizytaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SzczegolyWizytyDTO toDTO(SzczegolyWizyty szczegoly) {
        return SzczegolyWizytyDTO.builder()
                .szczegolyWizytyId(szczegoly.getSzczegolyWizytyId())
                .wizytaId(szczegoly.getWizyta().getWizytaId())
                .uslugaId(szczegoly.getUsluga().getUslugaId())
                .cenaLacznaUslugi(szczegoly.getCenaLacznaUslugi())
                .build();
    }
}
