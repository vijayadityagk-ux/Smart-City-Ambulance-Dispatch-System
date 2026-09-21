package com.smart_dispatch.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverApplication.class, args);
        System.out.println("\n🚑 DRIVER NAVIGATION JAVA UI IS LIVE AT http://localhost:8082\n");
    }
}
