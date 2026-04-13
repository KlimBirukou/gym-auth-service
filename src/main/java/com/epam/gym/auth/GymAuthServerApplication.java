package com.epam.gym.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GymAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymAuthServerApplication.class, args);
    }

}
