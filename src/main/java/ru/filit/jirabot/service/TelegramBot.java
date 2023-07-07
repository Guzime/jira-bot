package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.filit.jirabot.config.BotConfig;
import ru.filit.jirabot.model.type.CustomMsg;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final HandlerMessage handlerMessage;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (!Objects.isNull(update.getMessage())) {
                Message message = update.getMessage();
                log.info("Get message: {}", message);
                log.info("Get message: {} , from user: {}", message.getText(), message.getFrom().getUserName());
                SendMessage sendMessage = handlerMessage.parse(message);
                if (!sendMessage.getText().equals(CustomMsg.EMPTY.getText())) {
                    execute(sendMessage);
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}