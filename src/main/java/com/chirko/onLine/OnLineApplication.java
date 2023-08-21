package com.chirko.onLine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.chirko.onLine.repos.postgres")
@EnableRedisRepositories(basePackages = "com.chirko.onLine.repos.redis")
public class OnLineApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnLineApplication.class, args);
	}
}
