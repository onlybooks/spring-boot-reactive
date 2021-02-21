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

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@WebFluxTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
public class HypermediaItemControllerDocumentationTest {

	@Autowired private WebTestClient webTestClient;

	@MockBean InventoryService service;

	@MockBean ItemRepository repository;
	// end::intro[]

	// tag::test1[]
	@Test
	void findingAllItems() {
		when(repository.findAll()) //
				.thenReturn(Flux.just( //
						new Item("Alf alarm clock", //
								"nothing I really need", 19.99)));
		when(repository.findById((String) null)) //
				.thenReturn(Mono.just( //
						new Item("item-1", "Alf alarm clock", //
								"nothing I really need", 19.99)));

		this.webTestClient.get().uri("/hypermedia/items") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("findAll-hypermedia", //
						preprocessResponse(prettyPrint()))); //
	}
	// end::test1[]

	// tag::test2[]
	// @Test
	void postNewItem() {
		this.webTestClient.post().uri("/hypermedia/items") //
				.body(Mono.just( //
						new Item("item-1", "Alf alarm clock", //
								"nothing I really need", 19.99)),
						Item.class) //
				.exchange() //
				.expectStatus().isCreated() //
				.expectBody().isEmpty();
	}
	// end::test2[]

	// tag::test3[]
	@Test
	void findOneItem() {
		when(repository.findById("item-1")).thenReturn(Mono.just( //
				new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

		this.webTestClient.get().uri("/hypermedia/items/item-1") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()), //
						links( // <1>
								linkWithRel("self").description("이 `Item`에 대한 공식 링크"), // <2>
								linkWithRel("item").description("`Item` 목록 링크")))); // <3>
	}
	// end::test3[]

	@Test
	void findProfile() {
		this.webTestClient.get().uri("/hypermedia/items/profile") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("profile", //
						preprocessResponse(prettyPrint())));
	}
}
