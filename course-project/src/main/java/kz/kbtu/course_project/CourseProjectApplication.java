package kz.kbtu.course_project;

import kz.kbtu.course_project.config.ProfileProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProfileProperties.class) 
public class CourseProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseProjectApplication.class, args);
    }
}