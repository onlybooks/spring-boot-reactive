package com.greglturnquist.hackingspringboot.reactive;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication
@EnableHypermediaSupport(type = { HAL, HAL_FORMS })
public class HackingSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackingSpringBootApplication.class, args);
	}
}
