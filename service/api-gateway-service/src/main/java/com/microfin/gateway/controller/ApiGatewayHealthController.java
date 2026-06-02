package com.microfin.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGatewayHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "API Gateway Service is healthy!";
	}
}
