package kz.kbtu.course_project.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("gm")
public class GmCapitalService implements StartingCapitalService {
    @Override
    public int getStartingMoney() {
        return 100000;
    }
}
