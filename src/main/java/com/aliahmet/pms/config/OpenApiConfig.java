package com.aliahmet.pms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI patientManagementOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Patient Management System API")
                                .version("1.0.0")
                                .description(
                                        "REST API for patient admission, " +
                                                "medical test ordering, asynchronous " +
                                                "test execution and status tracking."
                                )
                );
    }
}