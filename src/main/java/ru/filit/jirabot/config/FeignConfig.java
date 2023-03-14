package ru.filit.jirabot.config;

import feign.Client;
import feign.auth.BasicAuthRequestInterceptor;
import feign.http2client.Http2Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Client client() {
        return new Http2Client();
    }
/*
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("rbxMOS003307", "Siebel202211");
        //return new BasicAuthRequestInterceptor("ntolmachev", "Forblachok1!");
    }*/
}
