package com.wqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApp {
    public static void main(String[] args) {

        SpringApplication.run(UserApp.class, args);
        System.out.println("Hello world!");
    }
}