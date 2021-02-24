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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;

/**
 * @author Greg Turnquist
 */
// tag::code[]
class Cart {

	private @Id String id;
	private List<CartItem> cartItems;

	private Cart() {}

	public Cart(String id) {
		this(id, new ArrayList<>());
	}

	public Cart(String id, List<CartItem> cartItems) {
		this.id = id;
		this.cartItems = cartItems;
	}
	// end::code[]

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Cart cart = (Cart) o;
		return Objects.equals(id, cart.id) && Objects.equals(cartItems, cart.cartItems);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, cartItems);
	}

	@Override
	public String toString() {
		return "Cart{" + "id='" + id + '\'' + ", cartItems=" + cartItems + '}';
	}
}
