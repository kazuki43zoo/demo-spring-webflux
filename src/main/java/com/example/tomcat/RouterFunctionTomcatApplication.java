package com.example.tomcat;

import java.time.LocalDateTime;

import javax.servlet.Servlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

public class RouterFunctionTomcatApplication {

	public static void main(String... args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		Context rootContext = tomcat.addContext("", System.getProperty("java.io.tmpdir"));
		Servlet httpHandlerServlet = new ServletHttpHandlerAdapter(createHandler());
		Tomcat.addServlet(rootContext, "dispatcher-handler", httpHandlerServlet);
		rootContext.addServletMappingDecoded("/", "dispatcher-handler");
		tomcat.start();
		tomcat.getServer().await();
	}

	private static HttpHandler createHandler() {
		RouterFunction<ServerResponse> routerFunction =
			route(GET("/"), Handlers::hello)
				.andRoute(GET("/now"), Handlers::now);
		return toHttpHandler(routerFunction);
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