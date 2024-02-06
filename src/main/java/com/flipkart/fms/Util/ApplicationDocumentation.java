package com.flipkart.fms.Util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition
public class ApplicationDocumentation {
	Contact contact() {
		return new Contact().name("Varasheb Kanthi")
				.url(null)
				.email("varkanthi456@gmail.com");
	}
	
	Info info () {
		return new Info().title("School-Board-API")
				.version("1.0v")
				.description("FlipKart-Clone-API is a RESTFUL API built using"+
						"Spring Boot and MySql database");
	}
	
	@Bean
	OpenAPI openAPI() {
		return new OpenAPI().info(info());
	}
}
