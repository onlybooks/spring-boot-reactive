/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greglturnquist.hackingspringboot.reactive;

import static org.assertj.core.api.Assertions.*;

import reactor.test.StepVerifier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Greg Turnquist
 */
// tag::setup[]
@SpringBootTest // <1>
@AutoConfigureWebTestClient // <2>
@Testcontainers // <3>
@ContextConfiguration // <4>
public class RabbitTest {

	@Container static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine"); // <5>

	@Autowired WebTestClient webTestClient; // <6>

	@Autowired ItemRepository repository; // <7>

	@DynamicPropertySource // <8>
	static void configure(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
		registry.add("spring.rabbitmq.port", container::getAmqpPort);
	}
	// end::setup[]

	// tag::spring-amqp-test[]
	@Test
	void verifyMessagingThroughAmqp() throws InterruptedException {
		this.webTestClient.post().uri("/items") // <1>
				.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
				.exchange() //
				.expectStatus().isCreated() //
				.expectBody();

		Thread.sleep(1500L); // <2>

		this.webTestClient.post().uri("/items") // <3>
				.bodyValue(new Item("Smurf TV tray", "nothing important", 29.99)) //
				.exchange() //
				.expectStatus().isCreated() //
				.expectBody();

		Thread.sleep(2000L); // <4>

		this.repository.findAll() // <5>
				.as(StepVerifier::create) //
				.expectNextMatches(item -> {
					assertThat(item.getName()).isEqualTo("Alf alarm clock");
					assertThat(item.getDescription()).isEqualTo("nothing important");
					assertThat(item.getPrice()).isEqualTo(19.99);
					return true;
				}) //
				.expectNextMatches(item -> {
					assertThat(item.getName()).isEqualTo("Smurf TV tray");
					assertThat(item.getDescription()).isEqualTo("nothing important");
					assertThat(item.getPrice()).isEqualTo(29.99);
					return true;
				}) //
				.verifyComplete();
	}
	// end::spring-amqp-test[]

}
