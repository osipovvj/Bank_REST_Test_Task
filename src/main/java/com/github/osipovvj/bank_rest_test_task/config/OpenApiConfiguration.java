package com.github.osipovvj.bank_rest_test_task.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank REST Test Task API")
                        .version("1.0")
                        .description("Документация API по техническому заданию.")
                        .contact(new Contact()
                                .email("support@effective-mobile.ru")
                                .name("Support Agent 1")
                        ));
    }
}
