package edu.chnu.recipes_api;

import org.springframework.boot.SpringApplication;

public class TestRecipesApiApplication {
    public static void main(String[] args) {
        SpringApplication.from(RecipesApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
