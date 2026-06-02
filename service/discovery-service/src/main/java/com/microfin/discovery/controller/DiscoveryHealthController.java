package com.microfin.discovery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscoveryHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "Discovery Service is healthy!";
	}
}
