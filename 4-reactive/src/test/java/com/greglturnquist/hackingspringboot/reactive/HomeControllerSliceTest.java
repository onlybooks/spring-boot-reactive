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
import static org.mockito.Mockito.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@WebFluxTest(HomeController.class) // <1>
public class HomeControllerSliceTest {

	@Autowired // <2>
	private WebTestClient client;

	@MockBean // <3>
	InventoryService inventoryService;

	@Test
	void homePage() {
		when(inventoryService.getInventory()).thenReturn(Flux.just( //
				new Item("id1", "name1", "desc1", 1.99), //
				new Item("id2", "name2", "desc2", 9.99) //
		));
		when(inventoryService.getCart("My Cart")) //
				.thenReturn(Mono.just(new Cart("My Cart")));

		client.get().uri("/").exchange() //
				.expectStatus().isOk() //
				.expectBody(String.class) //
				.consumeWith(exchangeResult -> {
					assertThat( //
							exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
					assertThat( //
							exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
				});
	}
}
// end::code[]
