package com.pes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pes.mapper")
public class PesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PesApplication.class, args);
    }
}