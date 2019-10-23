package com.server.spring.ws.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.server.spring.ws.api.model.ChatMessage;
import com.server.spring.ws.api.model.ChatRepository;
import com.server.spring.ws.api.model.User;
import com.server.spring.ws.api.model.UserRepository;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");        
        
    }
    
    public static void endpointCreated() {
    	logger.info("Endpoint created");
    }
    
    public static void userCreated(String u) {
    	logger.info(u + " created");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    	
    	logger.warn(event.getMessage().toString());
        
    }
}
