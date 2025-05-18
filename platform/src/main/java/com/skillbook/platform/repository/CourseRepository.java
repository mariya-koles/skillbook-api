package com.skillbook.platform.repository;

import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
    List<Course> findByCategory(String category);
}
