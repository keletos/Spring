package kz.kbtu.course_project.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("player")
public class PlayerCapitalService implements StartingCapitalService {
    @Override
    public int getStartingMoney() {
        return 2550;
    }
}
