package com.nimesa.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NimesaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NimesaApplication.class, args);
	}

}
