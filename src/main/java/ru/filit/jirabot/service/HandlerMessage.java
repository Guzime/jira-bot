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
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.CustomMsg;
import ru.filit.jirabot.model.type.StatusCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerMessage {

    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;
    private final ChatInfoMapper chatInfoMapper;
    private final HandlerCommandMessage handlerCommandMessage;
    private final HandlerCustomMessage handlerCustomMessage;

    public SendMessage parseCommand(Message message) {
        ChatStatus status = fetchChatStatus(message);
        String inputMessage = message.getText();
        SendMessage responseForCommand = handlerCommandMessage.parse(message);
        if (!responseForCommand.getText().equals(CustomMsg.EMPTY.getText())) {
            return responseForCommand;
        }
        SendMessage responseCustomMsg = handlerCustomMessage.parse(message.getChatId().toString(), status, inputMessage);
        if (!responseCustomMsg.getText().equals(CustomMsg.EMPTY.getText())) {
            return responseCustomMsg;
        }
        return messageMapper.formatText(message.getChatId().toString(), String.format(CustomMsg.EMPTY.getText(), inputMessage));
    }

    public ChatStatus fetchChatStatus(Message message) {
        ChatDto chat = notificationClientApp.getChat(message.getChatId().toString());
        log.info("Get exist chat: {}", chat);
        if (!chat.getResult().getCode().equals(StatusCode.JBOT_001.getCode())) {
            chat = notificationClientApp.addChat(chatInfoMapper.newChatInfo(message));
            log.info("Create new chat: {}", chat);
        }
        return ChatStatus.valueOf(chat.getData().getStatus());
    }


}
