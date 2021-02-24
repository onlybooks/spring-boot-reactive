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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Greg Turnquist
 */
@WebFluxTest(controllers = HomeController.class)
public class HomeControllerTest {

	@Autowired private WebTestClient webTestClient;

	@MockBean InventoryService service;

	@Test
	void verifyLoginPageBlocksAccess() {
		this.webTestClient.get().uri("/") //
				.exchange() //
				.expectStatus().isUnauthorized();
	}

	@Test
	@WithMockUser(username = "ada")
	void verifyLoginPageWorks() {
		when(this.service.getInventory()).thenReturn(Flux.just( //
				new Item("1", "Alf alarm clock", "kids clock", 19.99), //
				new Item("2", "Smurf TV tray", "kids TV tray", 24.99)));
		
		when(this.service.getCart(any())).thenReturn(Mono.just(new Cart("Test Cart")));

		this.webTestClient.get().uri("/") //
				.exchange() //
				.expectStatus().isOk();
	}
}
