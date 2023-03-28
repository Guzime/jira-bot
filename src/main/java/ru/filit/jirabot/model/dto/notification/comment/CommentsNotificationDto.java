package ru.filit.jirabot.model.dto.notification.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsNotificationDto {
    @JsonProperty("telegramsId")
    private Set<Long> telegramsId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("comments")
    Set<CommentNotificationDto> comments;
}
