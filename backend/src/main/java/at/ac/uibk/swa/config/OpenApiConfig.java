package at.ac.uibk.swa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// NOTE: Explicitly enable Swagger using Application Settings (https://reflectoring.io/dont-use-spring-profile-annotation/)
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true")
public class OpenApiConfig {
    public static final String BEARER_KEY = "bearer-key";

    @Bean
    public OpenAPI customOpenAPI() {
        // https://swagger.io/docs/specification/authentication/
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(BEARER_KEY))
                .components(new Components()
                        .addSecuritySchemes(BEARER_KEY,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
