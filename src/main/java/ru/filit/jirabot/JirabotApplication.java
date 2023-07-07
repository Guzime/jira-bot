package ru.filit.jirabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JirabotApplication {

	public static void main(String[] args) {SpringApplication.run(JirabotApplication.class, args);}

}
