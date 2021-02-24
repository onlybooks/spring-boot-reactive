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

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Configuration
public class SecurityConfig {
	// end::code[]

	// tag::reactive-user-details[]
	@Bean
	public ReactiveUserDetailsService userDetailsService(UserRepository repository) { // <1>
		return username -> repository.findByName(username) // <2>
				.map(user -> User.withDefaultPasswordEncoder() // <3>
						.username(user.getName()) //
						.password(user.getPassword()) //
						.authorities(user.getRoles().toArray(new String[0])) //
						.build()); // <4>
	}
	// end::reactive-user-details[]

	// tag::users[]
	@Bean
	CommandLineRunner userLoader(MongoOperations operations) {
		return args -> {
			operations.save(new com.greglturnquist.hackingspringboot.reactive.User( //
					"greg", "password", Arrays.asList("ROLE_USER")));
		};
	}
	// end::users[]
}
