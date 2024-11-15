package com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.config;
import com.metasoft.pointbarmetasoft.securitymanagement.application.services.CustomUserDetailsServiceImpl;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.filter.JwtAuthenticationFilter;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.filter.JwtAuthorizationFilter;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.utils.JwtUtils;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.config.ModelMapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsServiceImpl customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    public SecurityConfig(PasswordEncoder passwordEncoder, CustomUserDetailsServiceImpl customUserDetailsService, JwtUtils jwtUtils, JwtAuthorizationFilter jwtAuthorizationFilter, UserRepository userRepository, ModelMapperConfig modelMapperConfig) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.userRepository = userRepository;
        this.modelMapperConfig = modelMapperConfig;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager ) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, userRepository, modelMapperConfig) ;
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/sign-in");

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);

        http.cors(cors -> cors
                .configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(Arrays.asList("https://pointbar-renovation.web.app/", "http://localhost:4200"));
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                })
        );
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth->{
            auth.requestMatchers(
                    "/api/v1/auth/admin/sign-up",
                    "/api/v1/auth/employee/sign-up",
                    "/api/v1/auth/employee/update/**",
                    "/api/v1/auth/employee/delete/**",
                    "/api/v1/auth/client/sign-up",
                    "/api/v1/auth/sign-in",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
            ).permitAll();
            auth.anyRequest()
                    .authenticated();
        });
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(jwtAuthenticationFilter);
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
