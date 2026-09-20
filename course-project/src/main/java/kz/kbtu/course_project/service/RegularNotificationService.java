package kz.kbtu.course_project.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.features.discount", name = "enabled", havingValue = "false", matchIfMissing = true)
public class RegularNotificationService implements NotificationService {
    @Override
    public String getMessage() {
        return "Обычный режим работы. Действуют стандартные тарифы.";
    }
}
