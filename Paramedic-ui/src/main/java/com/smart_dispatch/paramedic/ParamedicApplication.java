package com.smart_dispatch.paramedic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParamedicApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParamedicApplication.class, args);
        System.out.println("\n🚑 PARAMEDIC JAVA UI IS LIVE AT http://localhost:8081\n");
    }
}
