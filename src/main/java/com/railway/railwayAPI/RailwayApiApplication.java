package com.railway.railwayAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RailwayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RailwayApiApplication.class, args);
	}
}
