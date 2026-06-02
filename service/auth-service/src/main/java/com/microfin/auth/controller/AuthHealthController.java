package com.microfin.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthHealthController {
	
	/**
	 * Health check endpoint to verify that the service is running.
	 * @return A simple message indicating the service is healthy.
	 */
	@GetMapping("/health")
	public String healthCheck() {
		return "Auth Service is healthy!";
	}

}
