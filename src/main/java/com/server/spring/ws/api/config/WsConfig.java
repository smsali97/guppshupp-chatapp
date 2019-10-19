package com.server.spring.ws.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.server.spring.ws.api.controller.WebSocketEventListener;

@Configuration
@EnableWebSocketMessageBroker
public class WsConfig implements WebSocketMessageBrokerConfigurer{

	String hostName = "localhost";
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("ws")
		.setAllowedOrigins("*")
		.withSockJS();
		WebSocketEventListener.endpointCreated();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic");
        
//		registry.enableStompBrokerRelay("/topic")
//        .setRelayHost(hostName)
//        .setRelayPort(61613)
//        .setClientLogin("guest")
//        .setClientPasscode("guest");
		
	}
	
	
}
