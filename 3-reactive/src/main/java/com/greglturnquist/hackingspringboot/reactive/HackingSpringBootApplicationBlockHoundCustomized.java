package com.greglturnquist.hackingspringboot.reactive;

import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.JdkIdGenerator;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringBootApplication
public class HackingSpringBootApplicationBlockHoundCustomized {

	// tag::blockhound[]
	public static void main(String[] args) {
		BlockHound.builder() // <1>
				.allowBlockingCallsInside( //
						TemplateEngine.class.getCanonicalName(), "process") // <2>
				.install(); // <3>

		SpringApplication.run(HackingSpringBootApplicationBlockHoundCustomized.class, args);
	}
	// end::blockhound[]
}
