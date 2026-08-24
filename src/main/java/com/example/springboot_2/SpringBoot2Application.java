package com.example.springboot_2;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBoot2Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext context =
                SpringApplication.run(SpringBoot2Application.class, args);

    }
}
