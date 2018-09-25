package com.fabiogouw.holder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HolderApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolderApplication.class, args);
	}
}
