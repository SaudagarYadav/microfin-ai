package com.microfin.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiChatHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "AI Chat Service is healthy!";
	}
}
