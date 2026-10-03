package kz.kbtu.course_project.controller;

import kz.kbtu.course_project.config.ProfileProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileCheckController {

    private final ProfileProperties profileProperties;

    public ProfileCheckController(ProfileProperties profileProperties) {
        this.profileProperties = profileProperties;
    }

    @GetMapping("/profile")
    public String checkDiscount() {
        return "Текущий профиль - " + profileProperties.mode() 
                + ", Начальные деньги = " + profileProperties.startingEddies();
    }
}

