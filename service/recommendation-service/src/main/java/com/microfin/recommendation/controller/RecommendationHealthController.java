package com.microfin.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "Recommendation Service is healthy!";
	}
}
