package com.server.spring.ws.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "chat_message")
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 510)
	private String content;

	@OneToOne
	private User sender;

	@OneToOne(optional = true)
	private User receiver;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm dd/MM/yy")
	private Date timestamp;

	private MessageType type;

	private Boolean delivered = false;

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public enum MessageType {
		CHAT, LEAVE, JOIN, FILE, STICKER
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getSender() {
		return sender;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public User getReceiver() {
		return receiver;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
