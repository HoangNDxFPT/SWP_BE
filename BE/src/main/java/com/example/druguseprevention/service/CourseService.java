package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import java.util.List;

public interface CourseService {
    List<Course> getCourses(String name);
    Course getCourseById(Long id);
    Course create(Course course);
    Course update(Long id, Course course);
    void delete(Long id);
    //Course restore(Long id); // nếu có chức năng khôi phục

}
