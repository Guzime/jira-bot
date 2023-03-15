package ru.filit.jirabot.mapper;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.type.ChatStatus;

import java.util.Objects;

@Service
public class ChatInfoMapper {

    public ChatInfo newChatInfo(Message message) {
        return ChatInfo.builder()
                .type(message.getChat().getType())
                .status(ChatStatus.HOLD.name())
                .title(Objects.isNull(message.getChat().getTitle()) ? message.getChat().getUserName() : message.getChat().getTitle())
                .telegramId(message.getChatId().toString())
                .build();
    }
}
