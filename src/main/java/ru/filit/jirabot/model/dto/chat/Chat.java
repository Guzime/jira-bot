package ru.filit.jirabot.model.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.filit.jirabot.model.dto.ResponseResult;

@Data
@Builder
@AllArgsConstructor
public class Chat {
    @JsonProperty("data")
    private ChatInfo data;
    @JsonProperty("result")
    private ResponseResult result;
}
