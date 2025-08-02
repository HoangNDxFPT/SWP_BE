package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.EnrollmentId;
import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {

    // Tìm enrollment của một user trong một khóa học
    Optional<Enrollment> findByMemberAndCourse(User member, Course course);

    // Lấy danh sách enrollment theo user
    List<Enrollment> findByMember(User member);

    // Lấy danh sách enrollment theo course
    List<Enrollment> findByCourse(Course course);

    // Truy vấn các khóa học mà user đã enroll
    @Query("SELECT e.course FROM Enrollment e WHERE e.member = :user")
    List<Course> findCoursesByUser(@Param("user") User user);

    // Truy vấn các user đã enroll trong một khóa học
    @Query("SELECT e.member FROM Enrollment e WHERE e.course = :course")
    List<User> findUsersByCourse(@Param("course") Course course);

    // Sửa tên field từ enrollmentDate thành enrollDate để match với entity
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.enrollDate >= :startDate AND e.enrollDate <= :endDate")
    long countByEnrollDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
