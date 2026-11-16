package kz.kbtu.course_project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CourseProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}

//$env:DB_PASSWORD = "YOUR_POSTGRES_PASSWORD".\mvnw.cmd test