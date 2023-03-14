package ru.filit.jirabot.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.filit.jirabot.api.NotificationClientApp;
import ru.filit.jirabot.config.WireMockConfiguration;
import ru.filit.jirabot.model.dto.chat.Chat;
import ru.filit.jirabot.model.dto.chat.ChatInfo;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ContextConfiguration(classes = {WireMockConfiguration.class})
public class HandleMessageTests {
    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private NotificationClientApp feignClient;

    @Test
    void processGetChatWithSuccessResponse() throws IOException {
        mockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/chat/string"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        HandleMessageTests.class
                                                .getClassLoader()
                                                .getResourceAsStream("payload/response-chat-data.json"),
                                        defaultCharset()))));
        assertEquals(feignClient.getChat("string").getData().getTelegramId(), "231852649");
    }

    @Test
    void processFindChat() throws IOException {
        mockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/chat/string"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        HandleMessageTests.class
                                                .getClassLoader()
                                                .getResourceAsStream("payload/response-chat-data.json"),
                                        defaultCharset()))));
        assertEquals(feignClient.getChat("string").getData().getTelegramId(), "231852649");
    }
}
