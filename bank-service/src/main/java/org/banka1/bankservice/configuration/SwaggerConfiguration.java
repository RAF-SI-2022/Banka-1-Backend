package org.banka1.bankservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    // Ovo treba da se promeni u naziv mikroservisa.
    private final String APP_TITLE = "bank-service";
    // Ovo treba da se promeni u neki opis mikroservisa.
    private final String APP_DESCRIPTION = "API za servis banke";

    // Ovo ispod ostavite kako jeste.
    private final String APP_API_VERSION = "1.0";
    private final String APP_LICENSE = "Licenca";
    private final String APP_LICENSE_URL = "Licenca";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title(APP_TITLE)
                        .description(APP_DESCRIPTION)
                        .version(APP_API_VERSION)
                        .license(new License().name(APP_LICENSE).url(APP_LICENSE_URL)));
    }

}
