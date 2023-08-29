package ru.filit.jirabot.model.dto.notification.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentNotificationDto {
    @JsonProperty("author")
    private String author;
    @JsonProperty("description")
    private String description;
}
