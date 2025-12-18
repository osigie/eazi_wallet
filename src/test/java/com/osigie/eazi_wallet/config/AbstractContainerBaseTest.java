package com.osigie.eazi_wallet.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBaseTest {


    static final PostgreSQLContainer MY_POSTGRES_CONTAINER;

    static {

        MY_POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("eazi_wallet")
                .withUsername("username")
                .withPassword("password");


        MY_POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_POSTGRES_CONTAINER::getPassword);

        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.url", MY_POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.flyway.user", MY_POSTGRES_CONTAINER::getUsername);
        registry.add("spring.flyway.password", MY_POSTGRES_CONTAINER::getPassword);

        // Hibernate configuration
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }
}
