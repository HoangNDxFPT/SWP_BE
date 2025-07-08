package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseQuizResultServiceImpl implements CourseQuizResultService {

    private final CourseQuizResultRepository repository;
    private final EnrollmentRepository enrollmentRepository;  // Thêm dòng này

    @Override
    public CourseQuizResult create(CourseQuizResult result) {
        CourseQuizResult savedResult = repository.save(result);

        //  Nếu đạt >= 60%, cập nhật trạng thái khóa học
        if (result.getScore() >= 0.6 * result.getTotalQuestions()) {
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByMemberAndCourse(
                    result.getUser(), result.getCourse());

            enrollmentOpt.ifPresent(enrollment -> {
                enrollment.setStatus(Enrollment.Status.Completed);
                enrollmentRepository.save(enrollment);
            });
        }

        return savedResult;
    }

    @Override
    public List<CourseQuizResult> findAll() {
        return repository.findAll();
    }

    @Override
    public CourseQuizResult findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    @Override
    public CourseQuizResult update(Long id, CourseQuizResult updatedResult) {
        CourseQuizResult existing = findById(id);
        existing.setScore(updatedResult.getScore());
        existing.setTotalQuestions(updatedResult.getTotalQuestions());
        existing.setCourse(updatedResult.getCourse());
        existing.setUser(updatedResult.getUser());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean isOwner(Long resultId, Long userId) {
        return repository.findById(resultId)
                .map(result -> result.getUser().getId().equals(userId))
                .orElse(false);
    }
}
