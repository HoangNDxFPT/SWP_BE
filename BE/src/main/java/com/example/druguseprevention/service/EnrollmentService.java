package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseDto;
import com.example.druguseprevention.dto.EnrollmentDto;
import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.User;

import java.util.List;

public interface EnrollmentService {
    Enrollment enrollUserToCourse(Long courseId);
    Enrollment enrollUserInCourse(User user, Course course);
    List<Enrollment> getEnrollmentsByUser(User user);
    List<Course> getCoursesByUser(User user);
    List<User> getUsersByCourse(Course course);

    // ✅ New methods
    List<EnrollmentDto> getEnrollmentDtosByUser(User user);
    List<EnrollmentDto> getEnrollmentDtosByCourse(Course course);
    Enrollment updateEnrollment(User user, Course oldCourse, Course newCourse);
    boolean unenrollUserFromCourse(User user, Course course);

    List<CourseDto> getCoursesOfCurrentUser(User currentUser);

    boolean cancelEnrollment(Long userId, Long courseId);
}
