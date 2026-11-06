package com.example.backend.config;

import com.example.backend.security.AuditInterceptor;
import com.example.backend.security.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Règles d'accès aux routes. Deux intercepteurs, dans cet ordre :
//  1. AuditInterceptor : trace chaque requête dans journal_audit (y compris celles refusées ensuite)
//  2. JwtInterceptor   : vérifie le jeton (les routes publiques sont listées dans JwtInterceptor)
// /error (page d'erreur interne de Spring) est exclue des deux.
// L'encodeur de mots de passe (BCrypt) reste déclaré dans PasswordConfig.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final AuditInterceptor auditInterceptor;
    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}
