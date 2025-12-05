package pl.fryzjer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Zakładu Fryzjerskiego")
                        .version("1.0.0")
                        .description("REST API do zarządzania zakładem fryzjerskim")
                        .contact(new Contact()
                                .name("Fryzjer App")
                                .email("kontakt@fryzjer.pl")));
    }
}
