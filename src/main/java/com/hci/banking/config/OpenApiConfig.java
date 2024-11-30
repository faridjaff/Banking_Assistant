package com.hci.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Spring Boot API").version("1.0.0"))
                .addServersItem(new Server().url("https://03a5-96-8-168-137.ngrok-free.app").description("Ngrok HTTPS Tunnel"));
    }
}
