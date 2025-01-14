package org.rb.cs2stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Cs2statsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cs2statsApplication.class, args);
	}

}
