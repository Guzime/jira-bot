package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.filit.jirabot.api.NotificationClientApp;
import ru.filit.jirabot.model.dto.chat.Chat;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.StatusCode;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleMessage {


    private final NotificationClientApp notificationClientApp;

    public SendMessage parseCommand(Message message){
        Chat chat = findChat(message);

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("hello")
                .build();
    }

    public Chat findChat(Message message) {
        Chat chat =  notificationClientApp.getChat(message.getChatId().toString());
        if (!chat.getResult().getCode().equals(StatusCode.JBOT_001.getCode())) {
            chat = notificationClientApp.addChat(
                    ChatInfo.builder()
                            .type(message.getChat().getType())
                            .status(ChatStatus.HOLD.name())
                            .title(Objects.isNull(message.getChat().getTitle()) ? message.getChat().getUserName() : message.getChat().getTitle())
                            .telegramId(message.getChatId().toString())
                            .build());
        }
        return chat;
    }


}
