package com.campus.sports;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.sports.mapper")
public class CampusSportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSportsApplication.class, args);
    }
}
