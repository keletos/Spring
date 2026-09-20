package kz.kbtu.course_project.controller;

import kz.kbtu.course_project.config.DiscountProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigCheckController {

    private final DiscountProperties discountProperties;

    public ConfigCheckController(DiscountProperties discountProperties) {
        this.discountProperties = discountProperties;
    }

    @GetMapping("/discount")
    public String checkDiscount() {
        return "Текущий профиль настроек: Скидка включена = " + discountProperties.enabled() 
                + ", Процент скидки = " + discountProperties.percentage() + "%";
    }
}

