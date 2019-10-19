package com.server.spring.ws.api.model;

import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<ChatMessage, Long> {
	
}
