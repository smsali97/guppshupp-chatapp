package com.server.spring.ws.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;
import com.server.spring.ws.api.model.ChatMessage;
import com.server.spring.ws.api.model.ChatMessage.MessageType;
import com.server.spring.ws.api.model.ChatRepository;
import com.server.spring.ws.api.model.User;
import com.server.spring.ws.api.model.UserRepository;

@Controller
@CrossOrigin(origins = "*")
public class ChatController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	ChatRepository chatRepository;
	
	@Value("${aws.ACCESS_KEY}")
	  String accessKey;
	@Value("${aws.SECRET_KEY}")
	  String secretKey;
	
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
	
	@SendTo("/topic/public")
	@MessageMapping("/chat.send")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		
		chatMessage.setDelivered(true);
		chatMessage.setTimestamp(new Date());
		
		ChatMessage newChatMessage = chatRepository.save(chatMessage);
		
		
		return newChatMessage;
		
		
		
	}
	
	
	
	@SendTo("/topic/private")
	@MessageMapping("/chat.send-private")
	public ChatMessage sendToPrivate(ChatMessage chatMessage ) {
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
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@ResponseBody
	public User getUser( @RequestParam String username) {
		
		Optional<User> opt =userRepository.findById(username);
		
		return opt.isPresent() ? opt.get() : null;
	}
	
	@RequestMapping(value = "/credentials", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getCredentials() {
		return Arrays.asList(new String[]{secretKey,accessKey});
	}
	
	@RequestMapping(value = "/chatMessages/public", method = RequestMethod.GET)
	@ResponseBody
	public List<ChatMessage> getPublicChatMessages() {
		
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		
		chatRepository.findAll().forEach( chat -> {
				if (chat.getReceiver() == null && !chat.getType().equals(MessageType.JOIN)) {
					list.add(chat);
				}
		});
		
		return list;
	}
	
	@RequestMapping(value = "/chatMessages/private", method = RequestMethod.POST)
	@ResponseBody
	public List<ChatMessage> getPrivateChatMessages(@RequestParam String user1, @RequestParam String user2 ) {
		
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		
		chatRepository.findAll().forEach( chat -> {
				if (chat.getReceiver() != null) {
					if ( (chat.getSender().getUsername().equals(user1) && chat.getReceiver().getUsername().equals(user2)) 
							|| (chat.getSender().getUsername().equals(user2) && chat.getReceiver().getUsername().equals(user1)) ) {
						list.add(chat);
					}
				}
		});
		
		return list;
	}

}
