package com.chat.api.configs.cripto;

import com.chat.api.configs.parameters.ArgonProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ArgonProperties.class)
public class PasswordEncoderConfig {

    @Bean
    public Argon2PasswordEncoder passwordEncoder(ArgonProperties props) {
        return new Argon2PasswordEncoder(
                props.saltLength(),
                props.hashLength(),
                props.parallelism(),
                props.memory(),
                props.iterations()
        );
    }
}