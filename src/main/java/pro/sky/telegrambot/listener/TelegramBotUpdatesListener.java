package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message =  (update.message() != null) ? update.message() : update.editedMessage();
            String text = message.text();
            long chatId = message.chat().id();

            if (text.equals("/start")) {
                telegramBot.execute(new SendMessage(chatId, "Привет, этот телеграмм бот поможет тебе создать напоминание. Напиши сообщение типа \"01.01.2022 20:00 Сделать домашнюю работу\"."));
            } else {
                Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                Matcher matcher = pattern.matcher(text);

                if(matcher.matches()) {
                    String dateTimeString = matcher.group(1);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    LocalDateTime date = LocalDateTime.parse(dateTimeString, formatter);

                    String task = matcher.group(3);

                    NotificationTask notificationTask = NotificationTask.builder()
                            .chatId(chatId)
                            .task(task)
                            .date(date)
                            .build();

                    notificationTaskRepository.save(notificationTask);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
