package com.hththn.dev.department_manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    //    http://localhost:8080/zen8labs-system/swagger-ui/index.html#/
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("API doc")
                        .version("1.0.0").description("description")
                        .license(new License().name("API License").url("http://domain.vn/license")))
                .servers(List.of(new Server().url("http://localhost:8080/department-system").description("System")))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi(){
        return GroupedOpenApi.builder()
                .group("api-service-1")
                .packagesToScan("com.trongdev.banking_system.controller")
                .build();
    }
}
