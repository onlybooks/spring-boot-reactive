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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Disabled("pom.xml에서 blockhound-junit-platform 의존 관계를 제거한 후에 실행해야 성공한다.")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) //<1>
@AutoConfigureWebTestClient // <2>
public class LoadingWebSiteIntegrationTest {

	@Autowired WebTestClient client; // <3>

	@Test // <4>
	void test() {
		client.get().uri("/").exchange() //
				.expectStatus().isOk() //
				.expectHeader().contentType(TEXT_HTML) //
				.expectBody(String.class) //
				.consumeWith(exchangeResult -> {
					assertThat(exchangeResult.getResponseBody()).contains("<a href=\"/add");
				});
	}
}
// end::code[]
