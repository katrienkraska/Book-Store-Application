package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Вимикаємо CSRF для Postman
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/registration").permitAll() // Дозволяємо реєстрацію
                        .anyRequest().authenticated() // Усі інші — з авторизацією
                )
                .httpBasic(Customizer.withDefaults()); // Сучасна форма для httpBasic()

        return http.build();
    }
}
