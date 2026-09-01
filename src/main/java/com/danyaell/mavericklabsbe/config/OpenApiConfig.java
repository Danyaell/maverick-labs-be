package com.danyaell.mavericklabsbe.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    private static final String PORTFOLIO_URL =
            "https://danyaell-martinez.vercel.app";

    private static final String REPOSITORY_URL =
            "https://github.com/Danyaell/maverick-labs-be";

    @Bean
    public OpenAPI maverickLabsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Maverick Labs API")
                        .version("v1")
                        .description("""
                                Fan-made educational and portfolio API for exploring
                                Mega Man X game data and analyzing player-defined routes.

                                Maverick Labs is not affiliated with or endorsed by Capcom.
                                """)
                        .contact(new Contact()
                                .name("Danyaell Martínez Ortiz")
                                .url(PORTFOLIO_URL)))
                .externalDocs(new ExternalDocumentation()
                        .description("Maverick Labs backend repository")
                        .url(REPOSITORY_URL));
    }
}