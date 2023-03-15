package ru.filit.jirabot.mapper;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class SendMessageMapper {

    public static final String MARKDOWN = "Markdown";
    public static final String START_SUBSCRIBE_MESSAGE = "Введите, пожалуйста, код тикета, на обновление которого Вы хотите подписаться, например, так:```RDBO-36853```";

    public SendMessage startSubscribe(String chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .parseMode(MARKDOWN)
                .text(START_SUBSCRIBE_MESSAGE)
                .build();
    }
}
