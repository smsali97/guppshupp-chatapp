package com.server.spring.ws.api;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.server"})
public class SpringBootWebsocketApplication {
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5"));
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebsocketApplication.class, args);
	}

}

