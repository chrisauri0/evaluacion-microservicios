package com.example.microservicio_posts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroservicioPostsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioPostsApplication.class, args);
	}

}
