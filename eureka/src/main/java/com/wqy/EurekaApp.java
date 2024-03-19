package com.wqy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApp {
    public static void main(String[] args) {

        SpringApplication.run(EurekaApp.class, args);
        System.out.println("Hello world!");
    }
}