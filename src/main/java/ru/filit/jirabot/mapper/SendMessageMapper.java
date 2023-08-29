package ru.filit.jirabot.mapper;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class SendMessageMapper {

    public static final String MARKDOWN = "Markdown";

    public SendMessage formatText(String chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .parseMode(MARKDOWN)
                .text(text)
                .build();
    }
}
