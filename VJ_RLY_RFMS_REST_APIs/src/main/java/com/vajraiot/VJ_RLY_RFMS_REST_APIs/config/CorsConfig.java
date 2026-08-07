package com.vajraiot.VJ_RLY_RFMS_REST_APIs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins( "http://192.168.1.14:8889", "http://localhost:5173", "http://192.168.1.25:5173", "http://192.168.1.26:8889", "http://vajraiot.co.in", "https://vajraiot.co.in", "http://122.175.45.16:80", "http://122.175.45.16:8889")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}