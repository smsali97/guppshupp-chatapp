package com.server.spring.ws.api.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.server.spring.ws.api.model.ChatMessage;
import com.server.spring.ws.api.model.ChatMessage.MessageType;
import com.server.spring.ws.api.model.ChatRepository;
import com.server.spring.ws.api.model.User;
import com.server.spring.ws.api.model.UserRepository;

@Controller
@CrossOrigin(origins = "http://localhost:8080")
public class ChatController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	ChatRepository chatRepository;
	
	@MessageMapping("/chat.register")
	@SendTo("/topic/public")
	public ChatMessage register(@Payload User user) {
		
		userRepository.save(user);
		
		WebSocketEventListener.userCreated(user.getUsername() + ", " + user.getPassword());
		
		ChatMessage cm = new ChatMessage();
		cm.setSender(user);
		cm.setTimestamp(new Date());
		cm.setContent(user.getUsername() + " just joined the chat!");
		cm.setType(MessageType.JOIN);
		
		return cm;
	}

	
	
	@MessageMapping("/chat.send")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		System.out.println(chatMessage.getContent());
		
		chatMessage.setDelivered(true);
		chatMessage.setTimestamp(new Date());
		
		return chatMessage;
	}

}
