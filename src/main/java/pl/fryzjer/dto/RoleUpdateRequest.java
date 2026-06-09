package pl.fryzjer.dto;

import lombok.Data;

// Klasa DTO RoleUpdateRequest służąca do przesyłania danych
// - enkapsulacja danych wejściowych i wyjściowych żądań API

@Data
public class RoleUpdateRequest {
    private String rola;
}
