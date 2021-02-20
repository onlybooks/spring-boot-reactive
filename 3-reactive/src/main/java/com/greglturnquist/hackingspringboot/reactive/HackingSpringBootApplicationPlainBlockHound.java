package com.greglturnquist.hackingspringboot.reactive;

import reactor.blockhound.BlockHound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HackingSpringBootApplicationPlainBlockHound {

	// tag::blockhound[]
	public static void main(String[] args) {
		BlockHound.install();

		SpringApplication.run(HackingSpringBootApplicationPlainBlockHound.class, args);
	}
	// end::blockhound[]
}
