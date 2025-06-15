package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.EnrollmentId;
import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    // Tìm enrollment của một user trong một khóa học
    Optional<Enrollment> findByMemberAndCourse(User member, Course course);

    // Lấy danh sách enrollment theo user
    List<Enrollment> findByMember(User member);
}

