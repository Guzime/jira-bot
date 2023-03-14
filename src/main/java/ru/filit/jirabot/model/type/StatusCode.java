package ru.filit.jirabot.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    JBOT_001("JBOT_001", "Success"),
    JBOT_002("JBOT_002", "Not found chat"),
    JBOT_003("JBOT_003", "Not found ticket");

    private final String code;
    private final String message;
}
