package ru.filit.jirabot.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResultException extends RuntimeException {

    private final LocalDateTime timestamp;
    private final String message;

    public ErrorResultException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
}
