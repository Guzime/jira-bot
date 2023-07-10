package ru.filit.jirabot;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JirabotApplication {

	@Generated
	public static void main(String[] args) {SpringApplication.run(JirabotApplication.class, args);}

}