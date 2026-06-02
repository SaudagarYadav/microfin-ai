package com.microfin.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "Portfolio Service is healthy!";
	}
}
