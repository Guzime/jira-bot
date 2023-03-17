package ru.filit.jirabot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.filit.jirabot.config.BotConfig;

import java.util.Objects;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private BotConfig config;
    @Autowired
    private HandlerMessage handlerMessage;
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
                //execute(handlerMessage.parseCommand(message));
                execute(handlerMessage.parseCommand(message));
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}