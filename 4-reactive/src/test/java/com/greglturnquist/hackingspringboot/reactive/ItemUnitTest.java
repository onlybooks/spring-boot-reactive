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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.greglturnquist.hackingspringboot.reactive.Item;

/**
 * @author Greg Turnquist
 */
// tag::code[]
class ItemUnitTest {

	@Test // <1>
	void itemBasicsShouldWork() {
		Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99); // <2>

		// Test various aspects using AssertJ <3>
		assertThat(sampleItem.getId()).isEqualTo("item1");
		assertThat(sampleItem.getName()).isEqualTo("TV tray");
		assertThat(sampleItem.getDescription()).isEqualTo("Alf TV tray");
		assertThat(sampleItem.getPrice()).isEqualTo(19.99);

		assertThat(sampleItem.toString()).isEqualTo( //
				"Item{id='item1', name='TV tray', description='Alf TV tray', price=19.99}");

		Item sampleItem2 = new Item("item1", "TV tray", "Alf TV tray", 19.99);
		assertThat(sampleItem).isEqualTo(sampleItem2);
	}
}
// end::code[]
