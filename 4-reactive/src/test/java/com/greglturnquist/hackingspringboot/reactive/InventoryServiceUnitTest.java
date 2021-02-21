/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.greglturnquist.hackingspringboot.reactive.Cart;
import com.greglturnquist.hackingspringboot.reactive.CartItem;
import com.greglturnquist.hackingspringboot.reactive.CartRepository;
import com.greglturnquist.hackingspringboot.reactive.InventoryService;
import com.greglturnquist.hackingspringboot.reactive.Item;
import com.greglturnquist.hackingspringboot.reactive.ItemRepository;

/**
 * @author Greg Turnquist
 */
// tag::extend[]
@ExtendWith(SpringExtension.class) // <1>
class InventoryServiceUnitTest { // <2>
	// end::extend[]

	// tag::class-under-test[]
	InventoryService inventoryService; // <1>

	@MockBean private ItemRepository itemRepository; // <2>

	@MockBean private CartRepository cartRepository; // <2>
	// end::class-under-test[]

	// tag::before[]
	@BeforeEach // <1>
	void setUp() {
		// Define test data <2>
		Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
		CartItem sampleCartItem = new CartItem(sampleItem);
		Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

		// Define mock interactions provided
		// by your collaborators <3>
		when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
		when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
		when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

		inventoryService = new InventoryService(itemRepository, cartRepository); // <4>
	}
	// end::before[]

	// tag::test[]
	@Test
	void addItemToEmptyCartShouldProduceOneCartItem() { // <1>
		inventoryService.addItemToCart("My Cart", "item1") // <2>
				.as(StepVerifier::create) // <3>
				.expectNextMatches(cart -> { // <4>
					assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
							.containsExactlyInAnyOrder(1); // <5>

					assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
							.containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99)); // <6>

					return true; // <7>
				}) //
				.verifyComplete(); // <8>
	}
	// end::test[]

	// tag::test2[]
	@Test
	void alternativeWayToTest() { // <1>
		StepVerifier.create( //
				inventoryService.addItemToCart("My Cart", "item1")) //
				.expectNextMatches(cart -> { // <4>
					assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
							.containsExactlyInAnyOrder(1); // <5>

					assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
							.containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99)); // <6>

					return true; // <7>
				}) //
				.verifyComplete(); // <8>
	}
	// end::test2[]

}
