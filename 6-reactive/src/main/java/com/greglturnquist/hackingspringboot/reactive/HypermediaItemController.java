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

import static org.springframework.hateoas.mediatype.alps.Alps.*;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@RestController
public class HypermediaItemController {

	private final ItemRepository repository;

	public HypermediaItemController(ItemRepository repository) {
		this.repository = repository;
	}
	// end::intro[]

	// tag::root[]
	@GetMapping("/hypermedia")
	Mono<RepresentationModel<?>> root() {
		HypermediaItemController controller = //
				methodOn(HypermediaItemController.class);

		Mono<Link> selfLink = linkTo(controller.root()).withSelfRel().toMono();

		Mono<Link> itemsAggregateLink = //
				linkTo(controller.findAll()) //
						.withRel(IanaLinkRelations.ITEM) //
						.toMono();

		return selfLink.zipWith(itemsAggregateLink) //
				.map(links -> Links.of(links.getT1(), links.getT2())) //
				.map(links -> new RepresentationModel<>(links.toList()));
	}
	// end::root[]

	// tag::find-all[]
	@GetMapping("/hypermedia/items")
	Mono<CollectionModel<EntityModel<Item>>> findAll() {

		return this.repository.findAll() //
				.flatMap(item -> findOne(item.getId())) //
				.collectList() //
				.flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class) //
						.findAll()).withSelfRel() //
								.toMono() //
								.map(selfLink -> CollectionModel.of(entityModels, selfLink)));
	}
	// end::find-all[]

	// tag::find-one[]
	@GetMapping("/hypermedia/items/{id}")
	Mono<EntityModel<Item>> findOne(@PathVariable String id) {
		HypermediaItemController controller = methodOn(HypermediaItemController.class); // <1>

		Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono(); // <2>

		Mono<Link> aggregateLink = linkTo(controller.findAll()) //
				.withRel(IanaLinkRelations.ITEM).toMono(); // <3>

		return Mono.zip(repository.findById(id), selfLink, aggregateLink) // <4>
				.map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3()))); // <5>
	}
	// end::find-one[]

	// tag::find-affordance[]
	@GetMapping("/hypermedia/items/{id}/affordances")
	// <1>
	Mono<EntityModel<Item>> findOneWithAffordances(@PathVariable String id) {
		HypermediaItemController controller = //
				methodOn(HypermediaItemController.class);

		Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel() //
				.andAffordance(controller.updateItem(null, id)) // <2>
				.toMono();

		Mono<Link> aggregateLink = linkTo(controller.findAll()).withRel(IanaLinkRelations.ITEM) //
				.toMono();

		return Mono.zip(repository.findById(id), selfLink, aggregateLink) //
				.map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
	}
	// end::find-affordance[]

	// tag::add-new-item[]
	@PostMapping("/hypermedia/items")
	Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item) {
		return item //
				.map(EntityModel::getContent) //
				.flatMap(this.repository::save) //
				.map(Item::getId) //
				.flatMap(this::findOne) //
				.map(newModel -> ResponseEntity.created(newModel //
						.getRequiredLink(IanaLinkRelations.SELF) //
						.toUri()).build());
	}
	// end::add-new-item[]

	// tag::update-item[]
	@PutMapping("/hypermedia/items/{id}") // <1>
	public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, // <2>
			@PathVariable String id) {
		return item //
				.map(EntityModel::getContent) //
				.map(content -> new Item(id, content.getName(), // <3>
						content.getDescription(), content.getPrice())) //
				.flatMap(this.repository::save) // <4>
				.then(findOne(id)) // <5>
				.map(model -> ResponseEntity.noContent() // <6>
						.location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
	}
	// end::update-item[]

	// tag::profile[]
	@GetMapping(value = "/hypermedia/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
	public Alps profile() {
		return alps() //
				.descriptor(Collections.singletonList(descriptor() //
						.id(Item.class.getSimpleName() + "-repr") //
						.descriptor(Arrays.stream( //
								Item.class.getDeclaredFields()) //
								.map(field -> descriptor() //
										.name(field.getName()) //
										.type(Type.SEMANTIC) //
										.build()) //
								.collect(Collectors.toList())) //
						.build())) //
				.build();
	}
	// end::profile[]
}
