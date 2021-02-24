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

import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;

/**
 * @author Greg Turnquist
 */
// tag::code[]
public class User {

	private @Id String id;
	private String name;
	private String password;
	private List<String> roles;

	private User() {} // <1>

	public User(String id, String name, String password, List<String> roles) { // <2>
		this.id = id;
		this.name = name;
		this.password = password;
		this.roles = roles;
	}

	public User(String name, String password, List<String> roles) { // <3>
		this.name = name;
		this.password = password;
		this.roles = roles;
	}
	// end::code[]

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) &&
			Objects.equals(name, user.name) &&
			Objects.equals(password, user.password) &&
			Objects.equals(roles, user.roles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, password, roles);
	}

	@Override
	public String toString() {
		return "User{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			", password='" + "*******" + '\'' +
			", roles=" + roles +
			'}';
	}
}
