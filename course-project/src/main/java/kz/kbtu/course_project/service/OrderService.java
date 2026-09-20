package kz.kbtu.course_project.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final NotificationService notificationService;

    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public String getOrderStatus() {
        return "Статус заказа: " + notificationService.getMessage();
    }
}
