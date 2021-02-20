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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author Greg Turnquist
 */
class LoggingReactorFlows {

    private static final Logger log = LoggerFactory.getLogger(LoggingReactorFlows.class);

    private ItemRepository itemRepository;

    LoggingReactorFlows(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    Mono<Double> findPriceByMethodReference(String id) {
        // tag::method-handle[]
        return itemRepository.findById(id)
            .map(Item::getPrice);
        // end::method-handle[]
    }

    Mono<Double> findPriceWithLogging(String id) {
        // tag::injected-logger[]
        return itemRepository.findById(id)
            .map(item -> {
                log.debug("Found item");
                return item.getPrice();
            });
        // end::injected-logger[]
    }

    Mono<Double> findPriceWithReactorLogging(String id) {
        // tag::reactor-logging[]
        return itemRepository.findById(id)
            .log("Found item")
            .map(Item::getPrice);
        // end::reactor-logging[]
    }
}
