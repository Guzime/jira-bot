package ru.filit.jirabot.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.filit.jirabot.api.NotificationClientApp;
import ru.filit.jirabot.config.WireMockConfiguration;
import ru.filit.jirabot.mapper.SendMessageMapper;
import ru.filit.jirabot.model.dto.chat.ChatDto;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ContextConfiguration(classes = {WireMockConfiguration.class})
public class HandleMessageTests {
    public static final String TELEGRAM_ID = "231852649";
    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private NotificationClientApp feignClient;

    @Autowired
    private HandleMessage handleMessage;


    @Test
    @DisplayName("Testing Mosk Server")
    void test1() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        assertEquals(feignClient.getChat(TELEGRAM_ID).getData().getTelegramId(), TELEGRAM_ID);
    }

    @Test
    @DisplayName("Find chat already exist in DB")
    void test2() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        ChatDto chat = handleMessage.fetchChatStatus(getInputMessage());
        assertEquals(chat.getData().getTelegramId(), TELEGRAM_ID);
    }


    @Test
    @DisplayName("Find chat if not exist in DB")
    void test3() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-not-found.json");
        setResponse(WireMock.post(WireMock.urlEqualTo("/chat")), "payload/response-chat-data-success.json");

        ChatDto chat = handleMessage.fetchChatStatus(getInputMessage());
        assertEquals(chat.getData().getTelegramId(), TELEGRAM_ID);
    }

    @Test
    @DisplayName("Send command /subscribe in new chat")
    void test4() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        setResponse(WireMock.patch(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        SendMessage sendMessage = handleMessage.parseCommand(getCommand("/subscribe"));
        assertEquals(sendMessage.getText(), SendMessageMapper.START_SUBSCRIBE_MESSAGE);
    }

    @Test
    @DisplayName("Send command /subscribe_list in new chat")
    void test5() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        setResponse(WireMock.patch(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        SendMessage sendMessage = handleMessage.parseCommand(getCommand("/subscribe"));
        assertEquals(sendMessage.getText(), SendMessageMapper.START_SUBSCRIBE_MESSAGE);
    }

    private void setResponse(MappingBuilder post, String name) throws IOException {
        mockServer.stubFor(post
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        HandleMessageTests.class
                                                .getClassLoader()
                                                .getResourceAsStream(name),
                                        defaultCharset()))));
    }


    private Message getInputMessage() {
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(Long.parseLong(TELEGRAM_ID));
        chat.setTitle("tolmachevski");
        chat.setType("private");
        message.setChat(chat);
        return message;
    }

    private Message getCommand(String command) {
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(Long.parseLong(TELEGRAM_ID));
        chat.setTitle("tolmachevski");
        chat.setType("private");
        message.setChat(chat);
        message.setText(command);
        return message;
    }
}
