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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.result.view.Rendering;

/**
 * @author Greg Turnquist
 */
@Controller
public class HomeController {

	private final InventoryService inventoryService;

	public HomeController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	// tag::user-cart[]
	@GetMapping
	Mono<Rendering> home(Authentication auth) { // <1>
		return Mono.just(Rendering.view("home.html") //
				.modelAttribute("items", this.inventoryService.getInventory()) //
				.modelAttribute("cart", this.inventoryService.getCart(cartName(auth)) // <2>
						.defaultIfEmpty(new Cart(cartName(auth)))) //
				.modelAttribute("auth", auth) // <3>
				.build());
	}
	// end::user-cart[]

	// tag::adjust-cart[]
	@PostMapping("/add/{id}")
	Mono<String> addToCart(Authentication auth, @PathVariable String id) {
		return this.inventoryService.addItemToCart(cartName(auth), id) //
				.thenReturn("redirect:/");
	}

	@DeleteMapping("/remove/{id}")
	Mono<String> removeFromCart(Authentication auth, @PathVariable String id) {
		return this.inventoryService.removeOneFromCart(cartName(auth), id) //
				.thenReturn("redirect:/");
	}
	// end::adjust-cart[]

	// tag::cartName[]
	private static String cartName(Authentication auth) {
		return auth.getName() + "'s Cart";
	}
	// end::cartName[]
}
