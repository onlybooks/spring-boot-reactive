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

import reactor.core.publisher.Flux;

/**
 * @author Greg Turnquist
 */
public class DishMetaphor {

	static
	// tag::service[]
	class KitchenService {

		Flux<Dish> getDishes() {
			// You could model a ChefService, but let's just
			// hard code some tasty dishes.
			return Flux.just( //
					new Dish("Sesame chicken"), //
					new Dish("Lo mein noodles, plain"), //
					new Dish("Sweet & sour beef"));
		}
	}
	// end::service[]

	// tag::simple-server[]
	class SimpleServer {

		private final KitchenService kitchen;

		SimpleServer(KitchenService kitchen) {
			this.kitchen = kitchen;
		}

		Flux<Dish> doingMyJob() {
			return this.kitchen.getDishes() //
					.map(dish -> Dish.deliver(dish));
		}

	}
	// end::simple-server[]

	static
	// tag::polite-server[]
	class PoliteServer {

		private final KitchenService kitchen;

		PoliteServer(KitchenService kitchen) {
			this.kitchen = kitchen;
		}

		Flux<Dish> doingMyJob() {
			return this.kitchen.getDishes() //
					.doOnNext(dish -> System.out.println("Thank you for " + dish + "!")) //
					.doOnError(error -> System.out.println("So sorry about " //
							+ error.getMessage())) //
					.doOnComplete(() -> System.out.println("Thanks for all your hard work!")) //
					.map(Dish::deliver);
		}

	}
	// end::polite-server[]

	class BusyServer {

		private final KitchenService kitchen;

		BusyServer(KitchenService kitchen) {
			this.kitchen = kitchen;
		}

		Flux<Dish> doingMyJob() {
			// tag::multiple-side-effects[]
			return this.kitchen.getDishes() //
					.doOnNext(dish -> {
						System.out.println("Thank you for " + dish + "!");
						System.out.println("Marking the ticket as done.");
						System.out.println("Grabbing some silverware.");
					}) //
					.map(this::deliver);
			// end::multiple-side-effects[]
		}

		Dish deliver(Dish dish) {
			return Dish.deliver(dish);
		}
	}

	class BusyServer2 {

		private final KitchenService kitchen;

		BusyServer2(KitchenService kitchen) {
			this.kitchen = kitchen;
		}

		Flux<Dish> doingMyJob() {
			// tag::multiple-side-effects2[]
			return this.kitchen.getDishes() //
					.doOnNext( //
							dish -> System.out.println("Thank you for " + dish + "!")) //
					.doOnNext( //
							dish -> System.out.println("Marking the ticket as done.")) //
					.doOnNext(dish -> System.out.println("Grabbing some silverware.")) //
					.map(this::deliver);
			// end::multiple-side-effects2[]
		}

		Dish deliver(Dish dish) {
			return Dish.deliver(dish);
		}
	}

	static
	// tag::dish[]
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

		public boolean isDelivered() {
			return delivered;
		}

		@Override
		public String toString() {
			return "Dish{" + //
					"description='" + description + '\'' + //
					", delivered=" + delivered + '}';
		}
	}
	// end::dish[]

	static
	// tag::example[]
	class PoliteRestaurant {

		public static void main(String... args) {
			PoliteServer server = //
					new PoliteServer(new KitchenService());

			server.doingMyJob().subscribe( //
					dish -> System.out.println("Consuming " + dish), //
					throwable -> System.err.println(throwable));
		}
	}
	// end::example[]

}
