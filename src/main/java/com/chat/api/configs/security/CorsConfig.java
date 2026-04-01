package com.chat.api.configs.security;

import com.chat.api.configs.parameters.FrontParameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(FrontParameter frontParam) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(frontParam.front().url()));
        configuration.setAllowedMethods(frontParam.front().methods());
        configuration.setAllowedHeaders(frontParam.front().allowedHeaders());
        configuration.setAllowCredentials(frontParam.front().allowCredentials());
        configuration.setMaxAge(frontParam.front().maxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
