package com.amar.resource_booking.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
    public OpenAPI resourceBookingApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resource Booking API")
                        .description("REST API for resource booking with JWT authentication and role-based access")
                        .version("1.0"));
    }

}
