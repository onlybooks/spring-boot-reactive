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

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@DataMongoTest // <1>
public class MongoDbSliceTest {

	@Autowired ItemRepository repository; // <2>

	@Test // <3>
	void itemRepositorySavesItems() {
		Item sampleItem = new Item( //
				"name", "description", 1.99);

		repository.save(sampleItem) //
				.as(StepVerifier::create) //
				.expectNextMatches(item -> {
					assertThat(item.getId()).isNotNull();
					assertThat(item.getName()).isEqualTo("name");
					assertThat(item.getDescription()).isEqualTo("description");
					assertThat(item.getPrice()).isEqualTo(1.99);

					return true;
				}) //
				.verifyComplete();
	}
}
// end::code[]
