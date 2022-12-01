package net.samitkumar.databufferdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class DatabufferDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabufferDemoApplication.class, args);
	}

}

@Configuration
class Config {
	@Value("${spring.application.services.get_string}")
	private String serviceHost;

	@Bean
	public WebClient stringWebClient(WebClient.Builder builder) {
		//return WebClient.create(serviceHost);
		//return builder.baseUrl(serviceHost).build(); //This will work if we enable spring.codec.max-in-memory-size=5MB
		return WebClient
				.builder()
				.baseUrl(serviceHost)
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
				.build();
	}
}

@Configuration
@RequiredArgsConstructor
class Routers {
	private final Services services;
	@Bean
	public RouterFunction router() {
		return RouterFunctions
				.route(GET("/string"), request -> ok().body(services.getString(), String.class));
	}
}

@Service
@RequiredArgsConstructor
@Slf4j
class Services {
	private final WebClient stringWebClient;
	public Mono<String> getString() {
		return stringWebClient
				.get()
				.uri(uriBuilder -> uriBuilder.path("/string").build())
				.retrieve()
				.bodyToMono(String.class)
				.doOnSuccess(s -> log.info("SUCESS"))
				.doOnError(e -> log.error("ERROR {}", e.getMessage()));
	}
}