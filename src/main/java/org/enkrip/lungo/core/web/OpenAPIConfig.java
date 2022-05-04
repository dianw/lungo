package org.enkrip.lungo.core.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenAPIConfig {
    @Bean
    @ConfigurationProperties("openapi")
    public OpenAPI openAPI(Environment environment) {
        return new OpenAPI();
    }
}
