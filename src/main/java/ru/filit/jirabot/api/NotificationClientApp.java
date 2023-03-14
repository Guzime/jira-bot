package ru.filit.jirabot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.filit.jirabot.config.FeignConfig;
import ru.filit.jirabot.model.dto.chat.Chat;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.dto.jira.JiraIssueInfoResponse;

@FeignClient(name="NotificationClientApp", url = "${url.notification-app.chat}", configuration = FeignConfig.class)
public interface NotificationClientApp {
    @GetMapping("/chat/{chatId}")
    Chat getChat(@PathVariable("chatId") String chatId);

    @PostMapping("/chat")
    Chat addChat(ChatInfo chatInfo);
}
