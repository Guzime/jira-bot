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
import ru.filit.jirabot.model.dto.chat.ChatDto;
import ru.filit.jirabot.model.type.CustomMessage;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ContextConfiguration(classes = {WireMockConfiguration.class})
public class HandlerMessageTests {
    public static final String TELEGRAM_ID = "231852649";

    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private NotificationClientApp feignClient;

    @Autowired
    private HandlerMessage handlerMessage;


    @Test
    @DisplayName("Testing Mosk Server")
    void test1() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        assertEquals(TELEGRAM_ID, feignClient.getChat(TELEGRAM_ID).getData().getTelegramId());
    }

    @Test
    @DisplayName("Find chat already exist in DB")
    void test2() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        ChatDto chat = handlerMessage.fetchChatStatus(getInputMessage());
        assertEquals(TELEGRAM_ID, chat.getData().getTelegramId());
    }


    @Test
    @DisplayName("Find chat if not exist in DB")
    void test3() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-not-found.json");
        setResponse(WireMock.post(WireMock.urlEqualTo("/chat")), "payload/response-chat-data-success.json");

        ChatDto chat = handlerMessage.fetchChatStatus(getInputMessage());
        assertEquals(TELEGRAM_ID, chat.getData().getTelegramId());
    }

    @Test
    @DisplayName("Send command /subscribe in new chat")
    void test4() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        setResponse(WireMock.patch(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("/subscribe"));
        assertEquals(CustomMessage.START_SUBSCRIBE_MESSAGE.getText(), sendMessage.getText());
    }

    @Test
    @DisplayName("Send command /subscribe_list in exist chat")
    void test5() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/subscribe/list/" + TELEGRAM_ID)), "payload/response-chat-list-data-many.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("/subscribe_list"));
        assertEquals("Этот чат подписан на тикеты:\n" +
                "[IN-229](https://jirahq.rosbank.rus.socgen:8443/browse/IN-229) статус - `Backlog`\n" +
                "[KMB-892](https://jirahq.rosbank.rus.socgen:8443/browse/KMB-892) статус - `Testing`\n" +
                "[IN-243](https://jirahq.rosbank.rus.socgen:8443/browse/IN-243) статус - `Backlog`", sendMessage.getText());
    }

    @Test
    @DisplayName("Send command /unsubscribe in exist chat")
    void test6() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");
        setResponse(WireMock.patch(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("/unsubscribe"));
        assertEquals(CustomMessage.START_UNSUBSCRIBE_MESSAGE.getText(), sendMessage.getText());
    }

    @Test
    @DisplayName("Send code ticket to unsubscribe in exist chat")
    void test7() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-unsub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/unsubscribe/IN-243")), "payload/response-unsubscribe-success.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN-243"));
        assertEquals("Тикет `IN-243` отписан", sendMessage.getText());
    }

    @Test
    @DisplayName("Send not found code ticket to unsubscribe in exist chat")
    void test8() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-unsub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/unsubscribe/IN-243")), "payload/response-unsubscribe-not-found.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN-243"));
        assertEquals("Тикета `IN-243` нету в списке подписок", sendMessage.getText());
    }

    @Test
    @DisplayName("Send not found code ticket to unsubscribe in exist chat")
    void test9() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-unsub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/unsubscribe/IN-243")), "payload/response-unsubscribe-not-found.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN-243"));
        assertEquals("Тикета `IN-243` нету в списке подписок", sendMessage.getText());
    }

    @Test
    @DisplayName("Send invalid code ticket to unsubscribe in exist chat")
    void test10() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-unsub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/unsubscribe/IN-243")), "payload/response-unsubscribe-not-found.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN243"));
        assertEquals(CustomMessage.VALID_ERROR_MESSAGE.getText(), sendMessage.getText());
    }

    @Test
    @DisplayName("Send invalid command in exist chat")
    void test11() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-success.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("/comand"));
        assertEquals(CustomMessage.EMPTY_MESSAGE.getText(), sendMessage.getText());
    }

    @Test
    @DisplayName("Send valid code ticket to subscribe in exist chat")
    void test12() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-sub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/subscribe/IN-243")), "payload/response-subscribe-already-exist.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN-243"));
        assertEquals("Дружище! этот чат чат уже подписан на тикет - `IN-243`\n" +
                "глянь список всех подписанных тикетов", sendMessage.getText());
    }

    @Test
    @DisplayName("Send invalid code ticket to subscribe in exist chat")
    void test13() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-sub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/subscribe/IN-243")), "payload/response-subscribe-already-exist.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN243"));
        assertEquals(CustomMessage.VALID_ERROR_MESSAGE.getText(), sendMessage.getText());
    }

    @Test
    @DisplayName("Send valid code ticket to subscribe success")
    void test14() throws IOException {
        setResponse(WireMock.get(WireMock.urlEqualTo("/chat/" + TELEGRAM_ID)), "payload/response-chat-data-status-sub.json");
        setResponse(WireMock.get(WireMock.urlEqualTo("/subscribe/IN-243")), "payload/response-subscribe-success.json");

        SendMessage sendMessage = handlerMessage.parseCommand(getMessage("IN-243"));
        assertEquals("Подписываюсь на тикет:\n" +
                "[IN-243](https://jirahq.rosbank.rus.socgen:8443/browse/IN-243)\n" +
                "Статус\n" +
                "`Backlog`",
                sendMessage.getText());
    }

    private void setResponse(MappingBuilder post, String name) throws IOException {
        mockServer.stubFor(post
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        HandlerMessageTests.class
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

    private Message getMessage(String text) {
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(Long.parseLong(TELEGRAM_ID));
        chat.setTitle("tolmachevski");
        chat.setType("private");
        message.setChat(chat);
        message.setText(text);
        return message;
    }
}
