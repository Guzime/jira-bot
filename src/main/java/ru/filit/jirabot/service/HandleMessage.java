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
import ru.filit.jirabot.model.dto.issue.IssueInfo;
import ru.filit.jirabot.model.dto.issue.IssueInfoDto;
import ru.filit.jirabot.model.dto.issue.IssueListDto;
import ru.filit.jirabot.model.type.ChatCommand;
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.CustomMessage;
import ru.filit.jirabot.model.type.StatusCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleMessage {


    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;
    private final ChatInfoMapper chatInfoMapper;

    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";

    public SendMessage parseCommand(Message message){
        ChatDto chat = fetchChatStatus(message);

        String inputMessage = message.getText();
        if (inputMessage.split("@")[0].equals(ChatCommand.SUBSCRIBE.getName())) {
            return startSubscribe(message);
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.SUBSCRIBE_LIST.getName())) {
            return listSubscribe(message);
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.UNSUBSCRIBE.getName())) {
            return startUnsubscribe(message);
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.HELP.getName())) {
            return  messageMapper.customMessage(message.getChatId().toString(), CustomMessage.HELP_MESSAGE.getText());
        }

        if (chat.getData().getStatus().equals(ChatStatus.START_UNSUBSCRIBE.name())) {
            return unsubscribe(message.getChatId().toString(), inputMessage);
        }

        return SendMessage.builder()
                .chatId(message.getChatId())
                .parseMode("Markdown")
                .text("hello")
                .build();
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

    private SendMessage startUnsubscribe(Message message) {
        log.info("Processing UNSUBSCRIBE for chat: {}, {}", message.getChatId(), message.getChat().getTitle());
        notificationClientApp.updateChat(message.getChatId().toString(), ChatInfo.builder().status(ChatStatus.START_UNSUBSCRIBE.name()).build());
        return messageMapper.customMessage(message.getChatId().toString(), CustomMessage.START_UNSUBSCRIBE_MESSAGE.getText());
    }

    private SendMessage listSubscribe(Message message) {
        String textMessage = CustomMessage.SUBSCRIBE_LIST_MESSAGE.getText();
        IssueListDto issues = notificationClientApp.getIssues(message.getChatId().toString());
        for (IssueInfo issue : issues.getData()) {
            textMessage += (String.format(CustomMessage.SUBSCRIBE_LIST_FORMAT_MESSAGE.getText(),
                    issue.getCode(),
                    JIRA_URL + issue.getCode(),
                    issue.getStatus()));
        }
        return messageMapper.customMessage(message.getChatId().toString(), textMessage);
    }

    public SendMessage startSubscribe(Message message) {
        log.info("Processing SUBSCRIBE for chat: {}, {}", message.getChatId(), message.getChat().getTitle());
        notificationClientApp.updateChat(message.getChatId().toString(), ChatInfo.builder().status(ChatStatus.START_SUBSCRIPE.name()).build());
        return messageMapper.customMessage(message.getChatId().toString(), CustomMessage.START_SUBSCRIBE_MESSAGE.getText());
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
