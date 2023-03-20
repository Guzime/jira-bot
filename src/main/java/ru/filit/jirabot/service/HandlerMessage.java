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
import ru.filit.jirabot.model.dto.issue.IssueInfoDto;
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.CustomMessage;
import ru.filit.jirabot.model.type.StatusCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerMessage {


    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;
    private final ChatInfoMapper chatInfoMapper;
    private final HandlerCommandMessage handlerCommandMessage;

    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";

    public SendMessage parseCommand(Message message){
        ChatDto chat = fetchChatStatus(message);
        String inputMessage = message.getText();
        SendMessage responseForCommand = handlerCommandMessage.parse(message);
        if (!responseForCommand.getText().equals(CustomMessage.EMPTY_MESSAGE.getText())) {
            return responseForCommand;
        }

        if (chat.getData().getStatus().equals(ChatStatus.START_UNSUBSCRIBE.name())) {
            return unsubscribe(message.getChatId().toString(), inputMessage);
        }

        if (chat.getData().getStatus().equals(ChatStatus.START_SUBSCRIBE.name())) {
            return subscribe(message.getChatId().toString(), inputMessage);
        }

        return messageMapper.customMessage(message.getChatId().toString(), String.format(CustomMessage.EMPTY_MESSAGE.getText(), inputMessage));
    }

    private SendMessage unsubscribe(String chatId, String inputMessage) {
        if (inputMessage.split("-").length == 2) {
            IssueInfoDto issueUnsubscribe = notificationClientApp.unsubscribeIssue(inputMessage);
            if (StatusCode.JBOT_003.getCode().equals(issueUnsubscribe.getResult().getCode())) {
                log.info("Ticket {} not found", inputMessage);
                return messageMapper.customMessage(chatId, String.format(CustomMessage.UNSUBSCRIBE_NOT_FOUND_MESSAGE.getText(), inputMessage));
            }
            log.info("Ticket {} unsubscribe", inputMessage);
            return messageMapper.customMessage(chatId, String.format(CustomMessage.UNSUBSCRIBE_SUCCESS_MESSAGE.getText(), inputMessage));
        }
        return messageMapper.customMessage(chatId, String.format(CustomMessage.VALID_ERROR_MESSAGE.getText(), inputMessage));
    }

    private SendMessage subscribe(String chatId, String inputMessage) {
        if (inputMessage.split("-").length == 2) {
            IssueInfoDto issueSubscribe = notificationClientApp.subscribeIssue(inputMessage);
            if (StatusCode.JBOT_004.getCode().equals(issueSubscribe.getResult().getCode())) {
                log.info("Ticket {} already exist", inputMessage);
                return messageMapper.customMessage(chatId, String.format(CustomMessage.SUBSCRIBE_ALREADY_EXIST_MESSAGE.getText(), inputMessage));
            }
            if (StatusCode.JBOT_001.getCode().equals(issueSubscribe.getResult().getCode())) {
                log.info("Ticket {} success subscribe", inputMessage);
                return messageMapper.customMessage(chatId,
                        String.format(CustomMessage.SUBSCRIBE_SUCCESS_MESSAGE.getText(),
                                inputMessage,
                                JIRA_URL + inputMessage,
                                issueSubscribe.getData().getStatus()));
            }
        }
        return messageMapper.customMessage(chatId, String.format(CustomMessage.VALID_ERROR_MESSAGE.getText(), inputMessage));
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
