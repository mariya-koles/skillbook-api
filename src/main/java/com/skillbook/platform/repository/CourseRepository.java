package com.skillbook.platform.repository;

import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);

    List<Course> findByCategory(String category);
}
