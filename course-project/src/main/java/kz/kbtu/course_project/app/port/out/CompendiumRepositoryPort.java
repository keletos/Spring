package kz.kbtu.course_project.app.port.out;

import kz.kbtu.course_project.app.model.CompendiumInfo;

public interface CompendiumRepositoryPort {

    CompendiumInfo fetchCompendiumInfo();
}