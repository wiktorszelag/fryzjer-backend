package pl.fryzjer.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String rola;
    private String kodDostepu;
}
