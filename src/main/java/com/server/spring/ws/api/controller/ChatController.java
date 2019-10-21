package com.server.spring.ws.api.controller;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
		
		chatMessage.setDelivered(true);
		chatMessage.setTimestamp(new Date());
		
		ChatMessage newChatMessage = chatRepository.save(chatMessage);
		
		
		return newChatMessage;
	}
	
	@RequestMapping(value = "/checkPassword", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> isValid( @RequestParam String username, @RequestParam String password ) {
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		
		Optional<User> opt =userRepository.findById(user.getUsername());
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("success", String.valueOf(opt.isPresent() && opt.get().getPassword().equals(user.getPassword())));
		
		return hm;
	}
	
	@RequestMapping(value = "/checkUsername", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> isValid( @RequestParam String username) {
		
		Optional<User> opt =userRepository.findById(username);
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("success", String.valueOf(opt.isPresent() ));
		
		return hm;
	}
	
	@RequestMapping(value = "/chatMessages/public", method = RequestMethod.GET)
	@ResponseBody
	public List<ChatMessage> getPublicChatMessages() {
		
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		
		chatRepository.findAll().forEach( chat -> {
				if (chat.getReceiver() == null) {
					list.add(chat);
				}
		});
		
		return list;
	}
	
	@RequestMapping(value = "/chatMessages/private", method = RequestMethod.GET)
	@ResponseBody
	public List<ChatMessage> getPrivateChatMessages(@RequestParam String sender) {
		
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		
		chatRepository.findAll().forEach( chat -> {
				if (chat.getReceiver() != null && chat.getReceiver().getUsername().equals(sender)) {
					list.add(chat);
				}
		});
		
		return list;
	}

}
