package com.example.fileUpload;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.xerces.parsers.SecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI myOpenAPI() {

            SecurityScheme securityScheme = new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER).name("Authorization");
            SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

            return new OpenAPI()
                    .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                    .security(Arrays.asList(securityRequirement));

        }

}