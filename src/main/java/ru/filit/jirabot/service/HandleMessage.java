package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.filit.jirabot.api.NotificationClientApp;
import ru.filit.jirabot.mapper.ChatInfoMapper;
import ru.filit.jirabot.mapper.SendMessageMapper;
import ru.filit.jirabot.model.dto.chat.ChatDto;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.type.ChatCommand;
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.StatusCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleMessage {


    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;
    private final ChatInfoMapper chatInfoMapper;

    public SendMessage parseCommand(Message message){
        ChatDto chat = fetchChatStatus(message);

        String inputMessage = message.getText();
        if (inputMessage.split("@")[0].equals(ChatCommand.SUBSCRIBE.getName())) {
            return startSubscribe(message);
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.SUBSCRIBE_LIST.getName())) {
        }

        return SendMessage.builder()
                .chatId(message.getChatId())
                .parseMode("Markdown")
                .text("hello")
                .build();
    }

    public SendMessage startSubscribe(Message message) {
        log.info("Processing SUBSCRIBE for chat: {}, {}", message.getChatId(), message.getChat().getTitle());
        notificationClientApp.updateChat(message.getChatId().toString(), ChatInfo.builder().status(ChatStatus.START_SUBSCRIPE.name()).build());
        return messageMapper.startSubscribe(message.getChatId().toString());
    }

    public ChatDto fetchChatStatus(Message message) {
        ChatDto chat =  notificationClientApp.getChat(message.getChatId().toString());
        log.info("Get exist chat: {}", chat);
        if (!chat.getResult().getCode().equals(StatusCode.JBOT_001.getCode())) {
            chat = notificationClientApp.addChat(chatInfoMapper.newChatInfo(message));
            log.info("Create new chat: {}", chat);
        }
        return chat;
    }


}
