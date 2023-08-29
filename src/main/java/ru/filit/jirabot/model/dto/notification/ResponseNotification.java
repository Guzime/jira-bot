package ru.filit.jirabot.model.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.filit.jirabot.model.dto.ResponseResult;

@Data
@AllArgsConstructor
@Builder
public class ResponseNotification {
    @JsonProperty("result")
    private ResponseResult result;
}
