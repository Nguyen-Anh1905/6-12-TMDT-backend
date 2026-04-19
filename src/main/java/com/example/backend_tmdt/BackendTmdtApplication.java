package com.example.backend_tmdt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendTmdtApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendTmdtApplication.class, args);
	}

}
