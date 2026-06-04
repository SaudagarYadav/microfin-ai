package com.microfin.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AiChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiChatServiceApplication.class, args);
	}

}