package ru.filit.jirabot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.filit.jirabot.model.dto.notification.ResponseNotification;
import ru.filit.jirabot.model.dto.notification.comment.CommentsNotificationDto;
import ru.filit.jirabot.model.dto.notification.issue.IssueNotificationDto;
import ru.filit.jirabot.service.SendlerNotification;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class NotificationController {
    private final SendlerNotification sendlerNotification;

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseNotification sendCommentNotification(@RequestBody CommentsNotificationDto notificationDto) {
        log.info("lol");
        return sendlerNotification.sendCommentNotification(notificationDto);
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.OK)
    public ResponseNotification sendIssueNotification(@RequestBody IssueNotificationDto notificationDto) {
        log.info("lol2");
        return sendlerNotification.sendIssueNotification(notificationDto);
    }

}
