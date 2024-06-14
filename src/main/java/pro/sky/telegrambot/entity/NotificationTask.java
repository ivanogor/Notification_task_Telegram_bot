package pro.sky.telegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class NotificationTask {
    @Id
    private long chatId;
    private String task;
    private LocalDateTime date;
}
