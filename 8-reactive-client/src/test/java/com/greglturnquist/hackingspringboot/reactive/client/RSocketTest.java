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
package com.greglturnquist.hackingspringboot.reactive.client;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Greg Turnquist
 */
// tag::setup[]
@SpringBootTest // <1>
@AutoConfigureWebTestClient // <2>
public class RSocketTest {

	@Autowired WebTestClient webTestClient; // <3>
	@Autowired ItemRepository repository; // <4>
	// end::setup[]

	// tag::fire-and-forget[]
	@Test
	void verifyRemoteOperationsThroughRSocketFireAndForget() throws InterruptedException {

		// Clean out the database
		this.repository.deleteAll() // <1>
				.as(StepVerifier::create) //
				.verifyComplete();

		// Create a new "item"
		this.webTestClient.post().uri("/items/fire-and-forget") // <2>
				.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
				.exchange() //
				.expectStatus().isCreated() // <3>
				.expectBody().isEmpty(); // <4>

		Thread.sleep(500); //

		// Verify the "item" has been added to MongoDB
		this.repository.findAll() // <5>
				.as(StepVerifier::create) //
				.expectNextMatches(item -> {
					assertThat(item.getId()).isNotNull();
					assertThat(item.getName()).isEqualTo("Alf alarm clock");
					assertThat(item.getDescription()).isEqualTo("nothing important");
					assertThat(item.getPrice()).isEqualTo(19.99);
					return true;
				}) //
				.verifyComplete();
	}
	// end::fire-and-forget[]

	@Test
	void verifyRemoteOperationsThroughRSocketRequestStream() //
			throws InterruptedException {
		// Clean out the database
		this.repository.deleteAll().block(); // <1>

		// Create 3 new "item"s
		List<Item> items = IntStream.rangeClosed(1, 3)
				.mapToObj(i -> new Item("name - " + i, "description - " + i, i)) // <2>
				.collect(Collectors.toList());

		this.repository.saveAll(items).blockLast(); // <3>


		// Get stream
		this.webTestClient.get().uri("/items/request-stream")
				.accept(MediaType.APPLICATION_NDJSON) // <4>
				.exchange() //
				.expectStatus().isOk()
				.returnResult(Item.class) // <5>
				.getResponseBody() // <6>
				.as(StepVerifier::create)
				.expectNextMatches(itemPredicate("1")) // <7>
				.expectNextMatches(itemPredicate("2"))
				.expectNextMatches(itemPredicate("3"))
				.verifyComplete(); // <8>
	}

	private Predicate<Item> itemPredicate(String num) {
		return item -> {
			assertThat(item.getName()).startsWith("name");
			assertThat(item.getName()).endsWith(num);
			assertThat(item.getDescription()).startsWith("description");
			assertThat(item.getDescription()).endsWith(num);
			assertThat(item.getPrice()).isPositive();
			return true;
		};
	}

	// tag::request-response[]
	@Test
	void verifyRemoteOperationsThroughRSocketRequestResponse() //
			throws InterruptedException {

		// Clean out the database
		this.repository.deleteAll() // <1>
				.as(StepVerifier::create) //
				.verifyComplete();

		// Create a new "item"
		this.webTestClient.post().uri("/items/request-response") // <2>
				.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
				.exchange() //
				.expectStatus().isCreated() // <3>
				.expectBody(Item.class) //
				.value(item -> {
					assertThat(item.getId()).isNotNull();
					assertThat(item.getName()).isEqualTo("Alf alarm clock");
					assertThat(item.getDescription()).isEqualTo("nothing important");
					assertThat(item.getPrice()).isEqualTo(19.99);
				});

		Thread.sleep(500); // <4>

		// Verify the "item" has been added to MongoDB
		this.repository.findAll() // <4>
				.as(StepVerifier::create) //
				.expectNextMatches(item -> {
					assertThat(item.getId()).isNotNull();
					assertThat(item.getName()).isEqualTo("Alf alarm clock");
					assertThat(item.getDescription()).isEqualTo("nothing important");
					assertThat(item.getPrice()).isEqualTo(19.99);
					return true;
				}) //
				.verifyComplete();
	}
	// end::request-response[]

}
