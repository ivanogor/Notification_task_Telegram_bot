package pro.sky.telegrambot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class NotificationTask {
    @Id
    private long chatId;
    private String message;
    private LocalDateTime date;
}
