package com.example.fileUpload;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI myOpenAPI() {

            Info info = new Info()
                    .title("업로드 파일 OLE객체 분리 API")
                    .version("1.0");


            SecurityScheme securityScheme = new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER).name("Authorization");
            SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

            return new OpenAPI()
                    .info(info)
                    .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                    .security(Arrays.asList(securityRequirement));

        }

}