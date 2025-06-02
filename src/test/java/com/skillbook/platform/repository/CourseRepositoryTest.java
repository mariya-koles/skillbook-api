package com.skillbook.platform.repository;

import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private User createInstructor() {
        User instructor = User.builder()
                .username("instructor1")
                .email("instructor@test.com")
                .password("password123")
                .role(Role.INSTRUCTOR)
                .build();
        return entityManager.persist(instructor);
    }

    @Test
    public void whenSaveCourse_thenReturnSavedCourse() {
        // given
        User instructor = createInstructor();
        Course course = Course.builder()
                .title("Java Basics")
                .description("Introduction to Java Programming")
                .category("Programming")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(90)
                .build();

        // when
        Course saved = courseRepository.save(course);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Java Basics");
        assertThat(saved.getInstructor().getId()).isEqualTo(instructor.getId());
    }

    @Test
    public void whenFindByInstructor_thenReturnCourses() {
        // given
        User instructor = createInstructor();
        Course course1 = Course.builder()
                .title("Python Basics")
                .description("Introduction to Python")
                .category("Programming")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(60)
                .build();

        Course course2 = Course.builder()
                .title("Advanced Python")
                .description("Advanced Python Topics")
                .category("Programming")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(2))
                .durationMinutes(120)
                .build();

        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.flush();

        // when
        List<Course> found = courseRepository.findByInstructor(instructor);

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Python Basics", "Advanced Python");
    }

    @Test
    public void whenFindByCategory_thenReturnCourses() {
        // given
        User instructor = createInstructor();
        Course course1 = Course.builder()
                .title("Yoga Basics")
                .description("Introduction to Yoga")
                .category("Fitness")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(60)
                .build();

        Course course2 = Course.builder()
                .title("Advanced Yoga")
                .description("Advanced Yoga Poses")
                .category("Fitness")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(2))
                .durationMinutes(90)
                .build();

        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.flush();

        // when
        List<Course> found = courseRepository.findByCategory("Fitness");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Course::getCategory)
                .containsOnly("Fitness");
    }
} 