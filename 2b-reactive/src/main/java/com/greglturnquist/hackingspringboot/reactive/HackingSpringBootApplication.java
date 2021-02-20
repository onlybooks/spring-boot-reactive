package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringBootApplication
public class HackingSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackingSpringBootApplication.class, args);
	}
}
