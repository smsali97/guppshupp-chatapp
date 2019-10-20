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

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private ChatController chatController;
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        
        chatRepository.findAll().forEach(chatMessage -> {
        	chatController.sendMessage(chatMessage);
        });
        
        
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
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//      
//        User user = userRepository.findById(username).get();
//        if(user != null) {
//            logger.info("User Disconnected : " + username);
//
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(ChatMessage.MessageType.LEAVE);
//            chatMessage.setSender(user);
//
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
    }
}
