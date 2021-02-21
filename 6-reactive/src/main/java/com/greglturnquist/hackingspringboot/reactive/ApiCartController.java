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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Greg Turnquist
 */
@RestController
public class ApiCartController {

    private final InventoryService service;

    public ApiCartController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/api/carts")
    Flux<Cart> findAll() {
        return this.service.getAllCarts()
            .switchIfEmpty(this.service.newCart());
    }

    @GetMapping("/api/carts/{id}")
    Mono<Cart> findOne(@PathVariable String id) {
        return this.service.getCart(id);
    }

    @PostMapping("/api/carts/{cartId}/add/{itemId}")
    Mono<Cart> addToCart(@PathVariable String cartId, @PathVariable String itemId) {
        return this.service.addItemToCart(cartId, itemId);
    }

    @DeleteMapping("/api/carts/{cartId}/remove/{itemId}")
    Mono<Cart> removeFromCart(@PathVariable String cartId, @PathVariable String itemId) {
        return this.service.removeOneFromCart(cartId, itemId);
    }
}
