package com.server.spring.ws.api.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String content;
	
	@OneToMany(mappedBy = "chat_message")
	private User sender;
	
	private LocalDateTime timestamp;
	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss");
	private MessageType type;

	public enum MessageType {
		CHAT, LEAVE, JOIN
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

}
