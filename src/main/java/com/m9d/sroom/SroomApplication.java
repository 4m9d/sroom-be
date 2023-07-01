package com.m9d.sroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/secure.properties")
public class SroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(SroomApplication.class, args);
	}

}