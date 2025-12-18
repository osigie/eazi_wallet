package com.osigie.eazi_wallet.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.url}")
    private String serverUrl;

    @Value("${swagger.server.description}")
    private String serverDescription;

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription(serverDescription);

        Contact myContact = new Contact();
        myContact.setName("Ken Osagie");
        myContact.setEmail("kenosagie88@gmail.com");

        Info information = new Info()
                .title("EaziWallet")
                .version("1.0")
                .description("A quick bootstrap of eazi wallet service.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}

