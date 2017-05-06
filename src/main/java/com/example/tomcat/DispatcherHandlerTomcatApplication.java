package com.example.tomcat;

import java.time.LocalDateTime;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.config.EnableWebFlux;

import reactor.core.publisher.Mono;

import javax.servlet.Servlet;

public class DispatcherHandlerTomcatApplication {

	public static void main(String... args) throws LifecycleException {
		try (ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class)) {
			applicationContext.registerShutdownHook();
			Tomcat tomcat = new Tomcat();
			Context rootContext = tomcat.addContext("", System.getProperty("java.io.tmpdir"));
			Servlet httpHandlerServlet = new ServletHttpHandlerAdapter(createHandler(applicationContext));
			Tomcat.addServlet(rootContext, "dispatcher-handler", httpHandlerServlet);
			rootContext.addServletMappingDecoded("/", "dispatcher-handler");
			tomcat.start();
			tomcat.getServer().await();
		}
	}

	private static HttpHandler createHandler(ApplicationContext applicationContext) {
		return DispatcherHandler.toHttpHandler(applicationContext);
	}

	@ComponentScan
	@EnableWebFlux
	static class AppConfig {}

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