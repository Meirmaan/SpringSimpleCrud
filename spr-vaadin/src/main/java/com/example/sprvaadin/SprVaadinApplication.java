package com.example.sprvaadin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SprVaadinApplication {  //https://www.baeldung.com/spring-boot-vaadin

	public static void main(String[] args) {
		SpringApplication.run(SprVaadinApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner loadData(PersonRepository perRep) {
		return (args) -> {
			// save a couple of customers
			perRep.save(new Person("Jack", "Bauer"));
			perRep.save(new Person("Chloe", "O'Brian"));
			perRep.save(new Person("Kim", "Bauer"));
			perRep.save(new Person("David", "Palmer"));
			perRep.save(new Person("Michelle", "Dessler"));
			perRep.save(new Person("Steve", "Jobs"));
			perRep.save(new Person("Bill", "Gates"));
		};
	}
			
}
