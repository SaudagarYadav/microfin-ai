package com.microfin.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "User Service is healthy!";
	}
}
