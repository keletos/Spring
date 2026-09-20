package kz.kbtu.course_project.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.features.discount", name = "enabled", havingValue = "true")
public class DiscountNotificationService implements NotificationService {
    @Override
    public String getMessage() {
        return "Внимание! Активирован режим распродаж и скидок!";
    }
}
