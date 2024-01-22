package net.savantly.mainbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import net.savantly.mainbot.repository.CustomRepositoryImpl;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { OAuth2ClientAutoConfiguration.class })
@EnableJpaRepositories (repositoryBaseClass = CustomRepositoryImpl.class)
@EnableAsync
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
