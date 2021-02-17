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
// tag::dish[]
package com.greglturnquist.hackingspringboot.reactive;

import java.util.Objects;

class Dish {

	private String description;
	private boolean delivered = false;

	public static Dish deliver(Dish dish) {
		Dish deliveredDish = new Dish(dish.description);
		deliveredDish.delivered = true;
		return deliveredDish;
	}

	Dish(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDelivered() {
		return delivered;
	}

	@Override
	public String toString() {
		return "Dish{" + //
				"description='" + description + '\'' + //
				", delivered=" + delivered + //
				'}';
	}
}
// end::dish[]
