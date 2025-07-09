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
    private final CourseQuizResultRepository courseQuizResultRepository;

    private final CourseQuizResultRepository repository;
    private final EnrollmentRepository enrollmentRepository;  // Thêm dòng này

    @Override
    public CourseQuizResult create(CourseQuizResult result) {
        CourseQuizResult savedResult = repository.save(result);

        //  Nếu đạt >= 80%, cập nhật trạng thái khóa học
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
    @Override
    public List<CourseQuizResultDetailDto> getResultDetails(Long quizResultId) {
        List<CourseQuizResultDetail> details = CourseQuizResultDetailRepository.findByQuizResultId(quizResultId);
        return details.stream().map(detail -> {
            CourseQuizResultDetailDto dto = new CourseQuizResultDetailDto();
            dto.setQuestion(detail.getQuestion());
            dto.setOptions(detail.getOptions());
            dto.setCorrectAnswer(detail.getCorrectAnswer());
            dto.setStudentAnswer(detail.getStudentAnswer());
            dto.setIsCorrect(detail.isCorrect());
            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public List<CourseQuizResult> findByUserId(Long userId) {
        return courseQuizResultRepository.findByUserId(userId);
    }



}
