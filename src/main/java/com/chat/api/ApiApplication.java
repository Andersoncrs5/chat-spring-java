package com.chat.api;

import com.chat.api.configs.parameters.FrontParameter;
import com.chat.api.configs.parameters.JwtParameter;
import com.chat.api.configs.parameters.RoleParameter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableConfigurationProperties({JwtParameter.class, FrontParameter.class, RoleParameter.class})
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
