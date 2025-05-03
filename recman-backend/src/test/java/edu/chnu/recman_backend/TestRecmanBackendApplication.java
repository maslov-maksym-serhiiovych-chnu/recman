package edu.chnu.recman_backend;

import org.springframework.boot.SpringApplication;

public class TestRecmanBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(RecmanBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
