package com.example.netty;

import java.time.LocalDateTime;

import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;

import io.netty.channel.Channel;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;
import static org.springframework.web.reactive.function.BodyInserters.*;

public class RouterFunctionNettyApplication {

	public static void main(String... args) throws InterruptedException {
		Channel channel = null;
		try {
			channel = HttpServer.create(8080).newHandler(createHandler()).block().channel();
			channel.closeFuture().sync();
		} finally {
			if (channel != null) {
				channel.eventLoop().shutdownGracefully();
			}
		}
	}

	private static ReactorHttpHandlerAdapter createHandler() {
		RouterFunction<ServerResponse> routerFunction =
			route(GET("/"), Handlers::hello)
				.andRoute(GET("/now"), Handlers::now);
		return new ReactorHttpHandlerAdapter(toHttpHandler(routerFunction));
	}

	private static class Handlers {
		private static Mono<ServerResponse> hello(ServerRequest req) {
			return ServerResponse.ok().body(fromObject("Hello World !!"));
		}
		private static Mono<ServerResponse> now(ServerRequest req) {
			return ServerResponse.ok()
				.body(fromPublisher(Mono.defer(() -> Mono.just("Now is " + LocalDateTime.now())), String.class));
		}
	}

}
