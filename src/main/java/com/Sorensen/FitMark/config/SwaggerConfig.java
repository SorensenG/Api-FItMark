package com.Sorensen.FitMark.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    @ConditionalOnProperty(name = "app.security.expose-swagger", havingValue = "true")
    public OpenAPI fitMarkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitMark API")
                        .description("API de registro e acompanhamento de treinos de musculação. " +
                                "Todos os endpoints protegidos requerem o header `Authorization: Bearer <accessToken>`.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token JWT obtido em POST /auth/login")));
    }
}
