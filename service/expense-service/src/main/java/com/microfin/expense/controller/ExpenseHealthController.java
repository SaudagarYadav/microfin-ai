package com.microfin.expense.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseHealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "Expense Service is healthy!";
	}
}
