package ru.filit.jirabot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class HandleMessage {

    public SendMessage parseCommand(Message message){
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("hello")
                .build();
    }


}
