package ru.filit.jirabot.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomMessage {
    START_SUBSCRIBE_MESSAGE("Введите, пожалуйста, код тикета, на обновление которого Вы хотите подписаться, например, так:```RDBO-36853```"),
    START_UNSUBSCRIBE_MESSAGE("Напишите код тикета, который нужно отписать от чата"),
    SUBSCRIBE_LIST_MESSAGE("Этот чат подписан на тикеты:"),
    UNSUBSCRIBE_SUCCESS_MESSAGE("Тикет `%s` отписан"),
    UNSUBSCRIBE_NOT_FOUND_MESSAGE("Тикета `%s` нету в списке подписок"),
    HELP_MESSAGE("Бог в помощь \uD83D\uDE4F"),
    SUBSCRIBE_LIST_FORMAT_MESSAGE("\n[%s](%s) статус - `%s`");

    private final String text;
}
