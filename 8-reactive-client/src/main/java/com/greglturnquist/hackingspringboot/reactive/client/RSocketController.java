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

import static io.rsocket.metadata.WellKnownMimeType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.*;

import java.net.URI;
import java.time.Duration;

import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@RestController // <1>
public class RSocketController {

	private final Mono<RSocketRequester> requester; // <2>

	public RSocketController(RSocketRequester.Builder builder) { // <3>
		this.requester = builder //
				.dataMimeType(APPLICATION_JSON) // <4>
				.metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString())) // <5>
				.connectTcp("localhost", 7000) // <6>
				.retry(5) // <7>
				.cache(); // <8>
	}
	// end::code[]

	// tag::request-response[]
	@PostMapping("/items/request-response") // <1>
	Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
		return this.requester //
				.flatMap(rSocketRequester -> rSocketRequester //
						.route("newItems.request-response") // <2>
						.data(item) // <3>
						.retrieveMono(Item.class)) // <4>
				.map(savedItem -> ResponseEntity.created( // <5>
						URI.create("/items/request-response")).body(savedItem));
	}
	// end::request-response[]

	@GetMapping(value = "/items/request-stream", produces = MediaType.APPLICATION_NDJSON_VALUE) // <1>
	Flux<Item> findItemsUsingRSocketRequestStream() {
		return this.requester //
				.flatMapMany(rSocketRequester -> rSocketRequester // <2>
						.route("newItems.request-stream") // <3>
						.retrieveFlux(Item.class) // <4>
						.delayElements(Duration.ofSeconds(1))); // <5>
	}

	// tag::fire-and-forget[]
	@PostMapping("/items/fire-and-forget")
	Mono<ResponseEntity<?>> addNewItemUsingRSocketFireAndForget(@RequestBody Item item) {
		return this.requester //
				.flatMap(rSocketRequester -> rSocketRequester //
						.route("newItems.fire-and-forget") // <1>
						.data(item) //
						.send()) // <2>
				.then( // <3>
						Mono.just( //
								ResponseEntity.created( //
										URI.create("/items/fire-and-forget")).build()));
	}
	// end::fire-and-forget[]

	// tag::request-stream[]
	@GetMapping(value = "/items", produces = TEXT_EVENT_STREAM_VALUE) // <1>
	Flux<Item> liveUpdates() {
		return this.requester //
				.flatMapMany(rSocketRequester -> rSocketRequester //
						.route("newItems.monitor") // <2>
						.retrieveFlux(Item.class)); // <3>
	}
	// end::request-stream[]
}
