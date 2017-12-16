package com.tanuj.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class ConciergeMain {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConciergeMain.class, args);

    }

}
