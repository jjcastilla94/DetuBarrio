package detubarrio.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI detuBarrioOpenApi() {
        final String bearerScheme = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("DeTuBarrio REST API")
                .description("API para gestión de comercios locales, reseñas y autenticación JWT")
                .version("v1.0")
                .contact(new Contact().name("Equipo DeTuBarrio").email("equipo@detubarrio.local"))
                .license(new License().name("Uso académico - TFG")))
            .addSecurityItem(new SecurityRequirement().addList(bearerScheme))
            .schemaRequirement(
                bearerScheme,
                new SecurityScheme()
                    .name(bearerScheme)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            );
    }
}
