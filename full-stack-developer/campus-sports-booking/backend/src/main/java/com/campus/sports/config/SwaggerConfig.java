package com.campus.sports.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("校园运动场地预约系统 API")
                .version("1.0.0")
                .description("Vue3 + Spring Boot + MyBatis-Plus 校园运动场地预约系统接口文档")
                .contact(new Contact()
                    .name("Campus Sports Team")
                    .email("admin@campus.edu"))
            );
    }
}
