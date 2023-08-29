package ru.filit.jirabot.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomMsg {
    START_SUB("Введите, пожалуйста, код тикета, на обновление которого Вы хотите подписаться, например, так:```RDBO-36853```"),
    START_UNSUB("Напишите код тикета, который нужно отписать от чата"),
    SUB_LIST("Этот чат подписан на тикеты:"),
    UNSUB_SUCCESS("Тикет `%s` отписан"),
    UNSUB_NOT_FOUND("Тикета `%s` нету в списке подписок"),
    SUB_EXIST("Дружище! этот чат чат уже подписан на тикет - `%s`\nглянь список всех подписанных тикетов"),
    SUB_ERROR("Дружище! Произошла ошибка, при подписке на тикет - `%s`\nпопроси Толмача глянуть логи!"),
    SUB_SUCCESS("Подписываюсь на тикет:\n[%s](%s)\nСтатус\n`%s`"),
    EMPTY("EMPTY"),
    VALID_ERROR("Шляпу написал \uD83D\uDE21"),
    HELP("Бог в помощь \uD83D\uDE4F"),
    DESCRIPTION_CHANGE("*Описание тикета* [%s](%s) *поменяли!*"),
    TITLE_CHANGE("*Заголовок тикета* [%s](%s) *поменяли!*"),
    STATUS_CHANGE("*Статус тикета* [%s](%s) *поменяли!* \n\nБыло: _%s_ \nСтало: _%s_"),
    ADD_COMMENT("В тикете [%s](%s)\n`%s`\nдобавил _комментарий:_\n `%s`"),
    SUB_LIST_FORMAT("\n[%s](%s) статус - `%s`");

    private final String text;
}
