package com.example.netty;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.DispatcherHandler;

import io.netty.channel.Channel;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

public class DispatcherHandlerNettyApplication {

	public static void main(String... args) throws InterruptedException {
		try (ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class)) {
			applicationContext.registerShutdownHook();
			Channel channel = null;
			try {
				channel = HttpServer.create(8080).newHandler(createHandler(applicationContext)).block().channel();
				channel.closeFuture().sync();
			} finally {
				if (channel != null) {
					channel.eventLoop().shutdownGracefully();
				}
			}
		}
	}

	private static ReactorHttpHandlerAdapter createHandler(ApplicationContext applicationContext) {
		return new ReactorHttpHandlerAdapter(DispatcherHandler.toHttpHandler(applicationContext));
	}

	@EnableWebFlux
	@ComponentScan
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