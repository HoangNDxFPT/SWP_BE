package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizResultDetailDto;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.CourseQuizResultDetail;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.repository.CourseQuizResultDetailRepository;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseQuizResultServiceImpl implements CourseQuizResultService {
    @Override
    public List<CourseQuizResult> findByUserId(Long userId) {
        return List.of();
    }

    private final CourseQuizResultDetailRepository courseQuizResultDetailRepository;
    private final CourseQuizResultRepository courseQuizResultRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public CourseQuizResult create(CourseQuizResult result) {
        CourseQuizResult savedResult = courseQuizResultRepository.save(result);

        // Nếu đạt >= 80%, cập nhật trạng thái hoàn thành khóa học
        if (result.getScore() >= 0.8 * result.getTotalQuestions()) {
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
        return courseQuizResultRepository.findAll();
    }

    @Override
    public CourseQuizResult findById(Long id) {
        return courseQuizResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    @Override
    public CourseQuizResult update(Long id, CourseQuizResult updatedResult) {
        CourseQuizResult existing = findById(id);
        existing.setScore(updatedResult.getScore());
        existing.setTotalQuestions(updatedResult.getTotalQuestions());
        existing.setCourse(updatedResult.getCourse());
        existing.setUser(updatedResult.getUser());
        return courseQuizResultRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        courseQuizResultRepository.deleteById(id);
    }

    @Override
    public boolean isOwner(Long resultId, Long userId) {
        return courseQuizResultRepository.findById(resultId)
                .map(result -> result.getUser().getId().equals(userId))
                .orElse(false);
    }

    @Override
    public List<CourseQuizResultDetailDto> getResultDetails(Long quizResultId) {
        List<CourseQuizResultDetail> details = courseQuizResultDetailRepository.findByQuizResultId(quizResultId);

        return details.stream().map(detail -> {
            CourseQuizResultDetailDto dto = new CourseQuizResultDetailDto();
            dto.setQuestion(detail.getQuestion());
            dto.setOptions(detail.getOptions());
            dto.setCorrectAnswer(detail.getCorrectAnswer());
            dto.setStudentAnswer(detail.getStudentAnswer());
            dto.setCorrect(detail.isCorrect()); // <-- dùng setCorrect() chứ KHÔNG phải setIsCorrect()
            return dto;
        }).collect(Collectors.toList());
    }
}
