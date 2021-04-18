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

package com.greglturnquist.hackingspringboot.reactive.server;

import reactor.core.publisher.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Controller // <1>
public class RSocketService {

	private final ItemRepository repository;
	// end::code[]
	private final EmitterProcessor<Item> itemProcessor;

	private final FluxSink<Item> itemSink;

	//  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
//	private final Sinks.Many<Item> itemsSink;

	// tag::code2[]
	public RSocketService(ItemRepository repository) {
		this.repository = repository; // <2>
		// end::code2[]
		// tag::code3[]
		this.itemProcessor = EmitterProcessor.create(); // <1>
		this.itemSink = this.itemProcessor.sink(); // <2>
		//  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
//		this.itemsSink = Sinks.many().multicast().onBackpressureBuffer();

	}
	// end::code3[]

	// tag::request-response[]
	@MessageMapping("newItems.request-response") // <1>
	public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item) { // <2>
		return this.repository.save(item) // <3>
				.doOnNext(savedItem -> this.itemSink.next(savedItem)); // <4>
				//  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
//				.doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem));
	}
	// end::request-response[]

	@MessageMapping("newItems.request-stream") // <1>
	public Flux<Item> findItemsViaRSocketRequestStream() { // <2>
		return this.repository.findAll() // <3>
				.doOnNext(this.itemSink::next); // <4>
		//  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
//				.doOnNext(this.itemsSink::tryEmitNext);
	}

	// tag::fire-and-forget[]
	@MessageMapping("newItems.fire-and-forget")
	public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item) {
		return this.repository.save(item) //
				.doOnNext(savedItem -> this.itemSink.next(savedItem)) //
				//  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
//				.doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem))
				.then();
	}
	// end::fire-and-forget[]

	// tag::monitor[]
	@MessageMapping("newItems.monitor") // <1>
	public Flux<Item> monitorNewItems() { // <2>
		return this.itemProcessor; // <3>
		//  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
//		return this.itemsSink.asFlux();
	}
	// end::monitor[]
}
