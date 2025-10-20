package com.kaiburr.task_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // Use @Configuration instead of @Component
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Use @Configuration, as it's meant to define configuration beans
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to ALL endpoints
                        .allowedOrigins("http://localhost:3000") // Explicitly allow the React origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow all CRUD methods + OPTIONS (preflight)
                        .allowedHeaders("*"); // Allow all headers
            }
        };
    }
}