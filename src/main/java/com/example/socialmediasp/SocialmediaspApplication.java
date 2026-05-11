
package com.example.socialmediasp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SocialmediaspApplication {
	public static void main(String[] args) {
		SpringApplication.run(SocialmediaspApplication.class, args);
	}
}