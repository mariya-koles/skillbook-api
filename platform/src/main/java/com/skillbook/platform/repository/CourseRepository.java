package com.skillbook.platform.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.skillbook.platform.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
