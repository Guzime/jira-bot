package ru.filit.jirabot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseNotification> sendCommentNotification(@RequestBody CommentsNotificationDto notificationDto) {
        log.info("Accept request to sendCommentNotification : {}", notificationDto);
        return ResponseEntity.ok(sendlerNotification.sendCommentNotification(notificationDto));
    }

    @PostMapping("/issue")
    public ResponseEntity<ResponseNotification> sendIssueNotification(@RequestBody IssueNotificationDto notificationDto) {
        log.info("Accept request to sendIssueNotification : {}", notificationDto);
        return ResponseEntity.ok(sendlerNotification.sendIssueNotification(notificationDto));
    }

}
