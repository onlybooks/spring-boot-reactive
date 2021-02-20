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

import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.greglturnquist.hackingspringboot.reactive.Item;

/**
 * @author Greg Turnquist
 */
@Service
public class FluentService {

	private final ReactiveFluentMongoOperations fluentMongoOperations;

	public FluentService(ReactiveFluentMongoOperations fluentMongoOperations) {
		this.fluentMongoOperations = fluentMongoOperations;
	}

	Flux<Item> searchFluently(String name, String description) {
		return fluentMongoOperations.query(Item.class) //
				.matching(query(where("name").is(name).and("description").is(description))) //
				.all();
	}
}
