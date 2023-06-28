package com.m9d.sroom.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI Config
 *
 */
// @Profile({ "dev", "local" })
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI commonApi(@Value("${springdoc.version}") String springdocVersion) {
        Info info = new Info()
                .title("SROOM 프로젝트")
                .version(springdocVersion)
                .description("RESTful API");


        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}