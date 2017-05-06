package com.example.war;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.support.AbstractAnnotationConfigDispatcherHandlerInitializer;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class MyWebApplicationInitializer extends AbstractAnnotationConfigDispatcherHandlerInitializer {

	@Override
	protected Class<?>[] getConfigClasses() {
		return new Class[] { AppConfig.class };
	}

	@ComponentScan
	@EnableWebFlux
	static class AppConfig {
	}

	@RestController
	static class MyController {
		@GetMapping("/")
		Mono<String> hello() {
			return Mono.just("Hello World !!");
		}
		@GetMapping("/now")
		Mono<String> now() {
			return Mono.defer(() -> Mono.just("Now is " + LocalDateTime.now()));
		}
	}

}