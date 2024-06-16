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

@Component
@RequiredArgsConstructor
public class ScheduledTaskComponent {
    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;
    private final Logger logger = LoggerFactory.getLogger(ScheduledTaskComponent.class);

    @Scheduled(fixedRate = 60000)
    public void checkDateOfNotification() {
        try {
            LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            List<NotificationTask> currentTimeList = notificationTaskRepository.findByDate(currentDate);
            if (!currentTimeList.isEmpty()) {
                logger.info("Found notificationTask with current date");
                sendMessages(currentTimeList);
            }
        }
        catch (Exception e){
            logger.error("Error checking date of notification", e);
        }

    }

    private void sendMessages(List<NotificationTask> currentTimeList) {
        currentTimeList
                .forEach(this::sendMessage);
    }

    private void sendMessage(NotificationTask notificationTask){
        try {
            telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getTask()));
            logger.info("Message sent to chatId: {}", notificationTask.getChatId());
        } catch (Exception e) {
            logger.error("Error sending message to chatId: {}", notificationTask.getChatId(), e);
        }
    }
}
