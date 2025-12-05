package pl.fryzjer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fryzjer.dto.WizytaDTO;
import pl.fryzjer.entity.*;
import pl.fryzjer.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WizytaService {

    private final WizytaRepository wizytaRepository;
    private final KlientRepository klientRepository;
    private final FryzjerRepository fryzjerRepository;
    private final UslugaRepository uslugaRepository;
    private final SzczegolyWizytyRepository szczegolyWizytyRepository;

    public List<WizytaDTO> findAll() {
        return wizytaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WizytaDTO findById(Integer id) {
        return wizytaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Wizyta nie znaleziona"));
    }

    public WizytaDTO create(WizytaDTO dto) {
        Klient klient = klientRepository.findById(dto.getKlientId())
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));
        Fryzjer fryzjer = fryzjerRepository.findById(dto.getFryzjerId())
                .orElseThrow(() -> new RuntimeException("Fryzjer nie znaleziony"));

        Wizyta wizyta = Wizyta.builder()
                .klient(klient)
                .fryzjer(fryzjer)
                .dataGodzinaRozpoczecia(dto.getDataGodzinaRozpoczecia())
                .czasTrwaniaCalkowity(dto.getCzasTrwaniaCalkowity())
                .dataRezerwacji(LocalDate.now())
                .build();

        wizyta = wizytaRepository.save(wizyta);

        if (dto.getUslugiIds() != null) {
            for (Integer uslugaId : dto.getUslugiIds()) {
                Usluga usluga = uslugaRepository.findById(uslugaId)
                        .orElseThrow(() -> new RuntimeException("Usługa nie znaleziona"));
                SzczegolyWizyty szczegoly = SzczegolyWizyty.builder()
                        .wizyta(wizyta)
                        .usluga(usluga)
                        .cenaLacznaUslugi(usluga.getCenaNetto().multiply(
                                usluga.getStawkaVat().divide(java.math.BigDecimal.valueOf(100))
                                        .add(java.math.BigDecimal.ONE)))
                        .build();
                szczegolyWizytyRepository.save(szczegoly);
            }
        }

        return toDTO(wizyta);
    }

    public WizytaDTO update(Integer id, WizytaDTO dto) {
        Wizyta wizyta = wizytaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wizyta nie znaleziona"));

        if (dto.getKlientId() != null) {
            Klient klient = klientRepository.findById(dto.getKlientId())
                    .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));
            wizyta.setKlient(klient);
        }

        if (dto.getFryzjerId() != null) {
            Fryzjer fryzjer = fryzjerRepository.findById(dto.getFryzjerId())
                    .orElseThrow(() -> new RuntimeException("Fryzjer nie znaleziony"));
            wizyta.setFryzjer(fryzjer);
        }

        wizyta.setDataGodzinaRozpoczecia(dto.getDataGodzinaRozpoczecia());
        wizyta.setCzasTrwaniaCalkowity(dto.getCzasTrwaniaCalkowity());

        return toDTO(wizytaRepository.save(wizyta));
    }

    public void delete(Integer id) {
        wizytaRepository.deleteById(id);
    }

    public List<WizytaDTO> findByKlientId(Integer klientId) {
        return wizytaRepository.findByKlientKlientId(klientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<WizytaDTO> findByFryzjerId(Integer fryzjerId) {
        return wizytaRepository.findByFryzjerFryzjerId(fryzjerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<WizytaDTO> findByFryzjerIdAndDateRange(Integer fryzjerId, LocalDateTime start, LocalDateTime end) {
        return wizytaRepository.findByFryzjerFryzjerIdAndDataGodzinaRozpoczeciaBetween(fryzjerId, start, end).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private WizytaDTO toDTO(Wizyta wizyta) {
        List<Integer> uslugiIds = null;
        if (wizyta.getSzczegolyWizyty() != null) {
            uslugiIds = wizyta.getSzczegolyWizyty().stream()
                    .map(s -> s.getUsluga().getUslugaId())
                    .collect(Collectors.toList());
        }

        return WizytaDTO.builder()
                .wizytaId(wizyta.getWizytaId())
                .klientId(wizyta.getKlient().getKlientId())
                .fryzjerId(wizyta.getFryzjer().getFryzjerId())
                .dataGodzinaRozpoczecia(wizyta.getDataGodzinaRozpoczecia())
                .czasTrwaniaCalkowity(wizyta.getCzasTrwaniaCalkowity())
                .dataRezerwacji(wizyta.getDataRezerwacji())
                .uslugiIds(uslugiIds)
                .build();
    }
}
