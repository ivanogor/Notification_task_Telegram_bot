package pro.sky.telegrambot.component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduledTaskComponent {
    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;
    private final Logger logger = LoggerFactory.getLogger(ScheduledTaskComponent.class);
    @Scheduled(fixedRate = 60000)
    public void checkDateOfNotification(){
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> taskList = notificationTaskRepository.findAll();
        List<NotificationTask> currentTimeList = taskList.stream()
                .filter(notificationTask -> currentDate.equals(notificationTask.getDate()))
                .collect(Collectors.toList());
        if (!currentTimeList.isEmpty()) {
            logger.info("Found notificationTask with current date");
            sendMessage(currentTimeList);
        }

    }

    private void sendMessage(List<NotificationTask> currentTimeList){
        currentTimeList
                .forEach(notificationTask -> {
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getTask()));
                    logger.info("Message sent chatId: " + notificationTask.getChatId());
                });
    }
}
