package com.example.backend.config;

import com.example.backend.security.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Règles d'accès aux routes : toutes les requêtes passent par JwtInterceptor,
// sauf /error (page d'erreur interne de Spring). Les routes publiques sont listées dans JwtInterceptor.
// L'encodeur de mots de passe (BCrypt) reste déclaré dans PasswordConfig.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}
