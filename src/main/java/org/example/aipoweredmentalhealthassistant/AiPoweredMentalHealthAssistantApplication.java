package org.example.aipoweredmentalhealthassistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.aipoweredmentalhealthassistant.mapper")
public class AiPoweredMentalHealthAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPoweredMentalHealthAssistantApplication.class, args);
    }

}
