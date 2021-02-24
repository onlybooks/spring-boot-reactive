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
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
	// end::code[]

	@Bean
	public ReactiveUserDetailsService userDetailsService(UserRepository repository) { // <1>
		return username -> repository.findByName(username) // <2>
				.map(user -> User.withDefaultPasswordEncoder() // <3>
						.username(user.getName()) //
						.password(user.getPassword()) //
						.authorities(user.getRoles().toArray(new String[0])) //
						.build()); // <4>
	}

	// tag::custom-policy[]
	@Bean
	SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) { //
		return http //
				.authorizeExchange(exchanges -> exchanges //
						.anyExchange().authenticated() //
						.and() //
						.httpBasic() //
						.and() //
						.formLogin()) //
				.csrf().disable() //
				.build();
	}
	// end::custom-policy[]

	// tag::users[]
	static String role(String auth) {
		return "ROLE_" + auth;
	}

	static final String USER = "USER";
	static final String INVENTORY = "INVENTORY";

	@Bean
	CommandLineRunner userLoader(MongoOperations operations) {
		return args -> {
			operations.save(new com.greglturnquist.hackingspringboot.reactive.User( //
					"greg", "password", Arrays.asList(role(USER))));

			operations.save(new com.greglturnquist.hackingspringboot.reactive.User( //
					"manager", "password", Arrays.asList(role(USER), role(INVENTORY))));
		};
	}
	// end::users[]
}
