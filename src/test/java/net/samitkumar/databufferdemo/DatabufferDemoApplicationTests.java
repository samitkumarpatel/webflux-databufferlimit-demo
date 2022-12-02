package net.samitkumar.databufferdemo;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockStubMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * WireMock with Springboot - https://cloud.spring.io/spring-cloud-contract/2.0.x/multi/multi__spring_cloud_contract_wiremock.html
 * Wiremock - https://wiremock.org/docs/
 * @DynamicPropertySource - https://www.baeldung.com/spring-dynamicpropertysource
 */
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class DatabufferDemoApplicationTests {

	WebTestClient.Builder builder;

	@Autowired
	ApplicationContext applicationContext;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	void setUp() {
		//webTestClient = WebTestClient.bindToServer().build();
		//webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
		builder = WebTestClient.bindToApplicationContext(applicationContext).configureClient();
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.application.services.get_string", () -> "http://localhost:${wiremock.server.port}");
	}

	@Test
	@DisplayName("/string Integration Test")
	void stringRouterTest() {
		//stubs are define in test/resources/mappings & test/resources/__files directory

		/*
		stubFor(get(urlEqualTo("/string"))
			.willReturn(aResponse()
                .withBodyFile("string.txt"))); */

		builder
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
				.build()
				.get()
				.uri("/string")
				.accept(MediaType.TEXT_PLAIN)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(String.class);
	}
}
