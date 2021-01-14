package com.governmentcio.dmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Provides the central entry point and "top level" class for the application.
 * 
 * @author William Drew
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class Application {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

	public static void main(String[] args) {

		LOG.info("Calling SpringApplication.run()...");
		SpringApplication.run(Application.class, args);

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {

		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}
}
