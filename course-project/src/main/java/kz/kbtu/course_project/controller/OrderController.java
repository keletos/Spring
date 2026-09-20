package kz.kbtu.course_project.controller;

import kz.kbtu.course_project.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order")
    public String checkOrder() {
        return orderService.getOrderStatus();
    }
}
