package ru.filit.jirabot.service;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.filit.jirabot.config.JiraClient;
import ru.filit.jirabot.model.entity.Chat;
import ru.filit.jirabot.model.entity.CommentInfo;
import ru.filit.jirabot.model.entity.IssueInfo;
import ru.filit.jirabot.model.repository.ChatRepository;
import ru.filit.jirabot.model.type.ChatCommand;
import ru.filit.jirabot.model.type.ChatStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerMessage {
    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";
    private final ChatRepository chatRepository;

    private final JiraClient jiraClient;


    public SendMessage parseCommand(Message message) {
        Chat chat = chatRepository
                .findByTelegramId(message.getChatId().toString())
                .orElseGet(() -> createChat(message));
        log.info("Get chat: {}", chat);
        String inputMessage = message.getText();
        if (inputMessage.split("@")[0].equals(ChatCommand.SUBSCRIBE.getName())) {
            log.info("Processing SUBSCRIBE for chat: {}, {}", chat.getId(), chat.getTitle());
            chat.setStatus(ChatStatus.START_SUBSCRIPE.name());
            chatRepository.save(chat);
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .parseMode("Markdown")
                    .text("Введите, пожалуйста, код тикета, на обновление которого Вы хотите подписаться, например, так:```RDBO-36853```")
                    .build();
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.SUBSCRIBE_LIST.getName())) {
            StringBuilder text = new StringBuilder("Этот чат подписан на тикеты:");
            for (IssueInfo subscribeIssue : chat.getSubscribeIssues()) {
                text.append(String.format("\n[%s](%s) статус - `%s`",
                        subscribeIssue.getIssueKey(),
                        JIRA_URL + subscribeIssue.getIssueKey(),
                        subscribeIssue.getStatus()));
            }
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .parseMode("Markdown")
                    .text(text.toString())
                    .build();
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.UNSUBSCRIBE.getName())) {
            chat.setStatus(ChatStatus.UNSUBSCRIBE.name());
            chatRepository.save(chat);
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .parseMode("Markdown")
                    .text("Напишите код тикета, который нужно отписать от чата")
                    .build();
        }
        if (inputMessage.split("@")[0].equals(ChatCommand.HELP.getName())) {
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .parseMode("Markdown")
                    .text("Бог в помощь \uD83D\uDE4F")
                    .build();
        }
        if (chat.getStatus().equals(ChatStatus.UNSUBSCRIBE.name())) {
            if (inputMessage.split("-").length == 2) {
                for (IssueInfo subscribeIssue : chat.getSubscribeIssues()) {
                    if (subscribeIssue.getIssueKey().equals(inputMessage)) {
                        chat.getSubscribeIssues().remove(subscribeIssue);
                        log.info("Ticket {} is deleted", inputMessage);
                        chatRepository.save(chat);
                        return SendMessage.builder()
                                .chatId(message.getChatId())
                                .parseMode("Markdown")
                                .text(String.format("Тикет `%s` отписан", inputMessage))
                                .build();
                    }
                }
                return SendMessage.builder()
                        .chatId(message.getChatId())
                        .parseMode("Markdown")
                        .text(String.format("Тикета `%s` нету в списке подписок", inputMessage))
                        .build();
            } else {
                return SendMessage.builder()
                        .chatId(message.getChatId())
                        .parseMode("Markdown")
                        .text(String.format("Шляпу написал \uD83D\uDE21", inputMessage))
                        .build();
            }

        }

        if (chat.getStatus().equals(ChatStatus.START_SUBSCRIPE.name())) {
            return subscribe(message, chat, inputMessage);
        }

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("hello")
                .build();
    }

    private SendMessage subscribe(Message message, Chat chat, String inputMessage) {
        StringBuilder text = new StringBuilder();
        SendMessage sendMessage = SendMessage.builder().chatId(message.getChatId()).parseMode("Markdown").text("").build();
        if (inputMessage.split("-").length == 2) {
            boolean existSubIssue = isExistSubIssue(chat, inputMessage);
            if (existSubIssue) {
                log.info("Ticket {} is already subscribed", inputMessage);
                text.append(String.format("Дружище! этот чат чат уже подписан на тикет - `%s`\nглянь список всех подписанных тикетов", inputMessage));
            }
            log.info("Processing START_SUBSCRIBE for chat: {}, {}", chat.getId(), chat.getTitle());
            try {
                Issue issue = jiraClient.getIssue(inputMessage);
                IssueInfo issueInfo = IssueInfo.builder()
                        .status(issue.getStatus().getName())
                        .issueKey(inputMessage)
                        .description(issue.getDescription())
                        .comments(new ArrayList<>())
                        .build();
                addedComments(chat, issue, issueInfo);
                chat.getSubscribeIssues().add(issueInfo);
                chat.setStatus(ChatStatus.SUBSCRIBE.name());
                chatRepository.save(chat);

                log.info("Issue save in DB: {}", issueInfo);
                text.append(String.format("Подписываюсь на тикет:\n[%s](%s)\nСтатус\n`%s`",
                        inputMessage,
                        JIRA_URL + inputMessage,
                        issue.getStatus().getName()));
            } catch (RestClientException e) {
                log.error("Error for issue key {}, error: {}", inputMessage, e.getMessage());
                text.append(String.format("Дружище! что-то не так с получением инфы по этому тикету: `%s`\nПосмотри логи.", inputMessage));
                for (ErrorCollection errorCollection : e.getErrorCollections()) {
                    text.append(String.format("\n ошибка:\n `%s`", errorCollection));
                }

            }
        } else {
            text.append("Шляпу написал \uD83D\uDE21");
        }
        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private static void addedComments(Chat chat, Issue issue, IssueInfo issueInfo) {
        for (Comment comment : issue.getComments()) {
            CommentInfo commentInfo = CommentInfo.builder()
                    .jiraId(Objects.requireNonNull(comment.getId()).toString())
                    .author(Objects.requireNonNull(comment.getAuthor()).getDisplayName())
                    .description(comment.getBody())
                    .build();
            log.info("Comment {} is added for chat {}", commentInfo, chat.getTitle());
            issueInfo.getComments().add(commentInfo);
        }
    }

    private static boolean isExistSubIssue(Chat chat, String inputMessage) {
        boolean existSubIssue = false;
        for (IssueInfo subscribeIssue : chat.getSubscribeIssues()) {
            if (subscribeIssue.getIssueKey().equals(inputMessage)) {
                existSubIssue = true;
            }
        }
        return existSubIssue;
    }

    private Chat createChat(Message message) {
        Chat chat = Chat.builder()
                .type(message.getChat().getType())
                .status(ChatStatus.NEW.name())
                .title(Objects.isNull(message.getChat().getTitle()) ? message.getChat().getUserName() : message.getChat().getTitle())
                .telegramId(message.getChatId().toString())
                .build();
        chatRepository.save(chat);
        log.info("New chat save in DB!");
        return chat;
    }


    public ReplyKeyboardMarkup createButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add("Testing 1");
        row.add("Testing 2");

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
}
