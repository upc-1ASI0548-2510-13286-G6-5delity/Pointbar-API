package com.metasoft.pointbarmetasoft.shared.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        //Define the origin, methods, and headers that are allowed
        //Define the origin
        String originCors= "http://localhost:4200";

        config.setAllowCredentials(true);
        config.addAllowedOrigin(originCors);

        //Define the headers
        config.addAllowedHeader("*");

        //Define the methods
        config.addAllowedMethod("*");

        //Define the path
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
