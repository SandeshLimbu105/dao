package org.texas.systembdao.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI daoServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("System B — DAO API")
                        .version("1.0")
                        .description("""
                            Government Service Interoperability Prototype.

                            **System B (District Administration Office)** consumes
                            citizen DOB from System A (Ward Office) via REST API.

                            Authentication: JWT Bearer token (from `/api/auth/login`).
                            """)
                        .contact(new Contact()
                                .name("Government Interoperability Prototype")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT returned by /api/auth/login")));
    }
}