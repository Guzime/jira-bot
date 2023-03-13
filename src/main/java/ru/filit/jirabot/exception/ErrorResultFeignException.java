package ru.filit.jirabot.exception;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.filit.jirabot.model.dto.ResponseResult;

@Getter
public class ErrorResultFeignException extends RuntimeException {

    private final ResponseResult result;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ErrorResultFeignException(@JsonProperty("result") ResponseResult result) {
        this.result = result;
    }
}