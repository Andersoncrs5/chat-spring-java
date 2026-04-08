package com.chat.api;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
	}

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDBContainer() {
		MongoDBContainer container =
				new MongoDBContainer(DockerImageName.parse("mongo:6.0"))
						.withCommand("--replSet rs0");

		container.start();

		try {
			container.execInContainer(
					"mongosh",
					"--eval",
					"rs.initiate()"
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return container;
	}

	@Bean
	@ServiceConnection
	RedisContainer redisContainer() {
		return new RedisContainer(DockerImageName.parse("redis:latest"));
	}

}
