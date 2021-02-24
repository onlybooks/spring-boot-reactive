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

import reactor.core.publisher.Mono;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
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
	Mono<Rendering> home( //
			@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
			@AuthenticationPrincipal OAuth2User oauth2User) { // <1>
		return Mono.just(Rendering.view("home.html") //
				.modelAttribute("items", this.inventoryService.getInventory()) //
				.modelAttribute("cart", this.inventoryService.getCart(cartName(oauth2User)) // <2>
						.defaultIfEmpty(new Cart(cartName(oauth2User)))) //

				// Fetching authentication details is a little more complex
				.modelAttribute("userName", oauth2User.getName()) //
				.modelAttribute("authorities", oauth2User.getAuthorities()) //
				.modelAttribute("clientName", //
						authorizedClient.getClientRegistration().getClientName()) //
				.modelAttribute("userAttributes", oauth2User.getAttributes()) //
				.build());
	}
	// end::user-cart[]

	// tag::adjust-cart[]
	@PostMapping("/add/{id}")
	Mono<String> addToCart(@AuthenticationPrincipal OAuth2User oauth2User, @PathVariable String id) {
		return this.inventoryService.addItemToCart(cartName(oauth2User), id) //
				.thenReturn("redirect:/");
	}

	@DeleteMapping("/remove/{id}")
	Mono<String> removeFromCart(@AuthenticationPrincipal OAuth2User oauth2User, @PathVariable String id) {
		return this.inventoryService.removeOneFromCart(cartName(oauth2User), id) //
				.thenReturn("redirect:/");
	}
	// end::adjust-cart[]

	// tag::inventory[]
	@PostMapping
	@ResponseBody
	Mono<Item> createItem(@RequestBody Item newItem) {
		return this.inventoryService.saveItem(newItem);
	}

	@DeleteMapping("/{id}")
	@ResponseBody
	Mono<Void> deleteItem(@PathVariable String id) {
		return this.inventoryService.deleteItem(id);
	}
	// end::inventory[]

	// tag::cartName[]
	private static String cartName(OAuth2User oAuth2User) {
		return oAuth2User.getName() + "'s Cart";
	}
	// end::cartName[]
}
