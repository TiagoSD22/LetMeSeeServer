package com.letmesee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.letmesee.repositorio.ImagemRepositorio;

@SpringBootApplication
@ComponentScan ({"com.letmesee.controller","com.letmesee.service"})
@EntityScan("com.letmesee.entity")
@EnableJpaRepositories("com.letmesee.repositorio")
public class LetMeSeeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LetMeSeeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
