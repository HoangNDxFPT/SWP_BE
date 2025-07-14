package com.example.druguseprevention.service;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.Course;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.CourseQuizResultDetail;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseQuizResultDetailRepository;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import com.example.druguseprevention.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseQuizResultServiceImpl implements CourseQuizResultService {

    private final CourseQuizResultDetailRepository courseQuizResultDetailRepository;
    private final CourseQuizResultRepository courseQuizResultRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

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
    public List<CourseQuizResult> findByUserId(Long userId) {
        return courseQuizResultRepository.findByUserId(userId);
    }

    @Override
    public CourseQuizResultFullResponse submitQuizAndReturn(QuizSubmitRequest request, User user) {
        // 1. Tạo kết quả quiz
        CourseQuizResult result = new CourseQuizResult();
        result.setUser(user);
        result.setCourse(request.getCourseId() != null ? courseRepository.findById(request.getCourseId()).orElseThrow() : null);
        result.setScore((int) request.getScore());
        result.setTotalQuestions(request.getAnswers().size());
        CourseQuizResult savedResult = courseQuizResultRepository.save(result);

        // 2. Lưu từng câu trả lời
        for (QuizAnswerDto dto : request.getAnswers()) {
            CourseQuizResultDetail detail = new CourseQuizResultDetail();
            detail.setQuestion(dto.getQuestion());
            detail.setOptions(dto.getOptions().toString());
            detail.setCorrectAnswer(dto.getCorrectAnswer());
            detail.setStudentAnswer(dto.getStudentAnswer());
            detail.setCorrect(dto.getCorrectAnswer().equals(dto.getStudentAnswer()));
            detail.setQuizResult(savedResult);
            courseQuizResultDetailRepository.save(detail);
        }

        // 3. Cập nhật trạng thái khóa học nếu >= 80%
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByMemberAndCourse(user, savedResult.getCourse());
        if (savedResult.getScore() >= 0.8 * savedResult.getTotalQuestions()) {
            enrollmentOpt.ifPresent(enrollment -> {
                enrollment.setStatus(Enrollment.Status.Completed);
                enrollmentRepository.save(enrollment);
            });
        }

        // 4. Tạo response
        CourseQuizResultFullResponse response = new CourseQuizResultFullResponse();
        response.setId(savedResult.getId());
        response.setScore(savedResult.getScore());
        response.setTotalQuestions(savedResult.getTotalQuestions());
        response.setSubmittedAt(savedResult.getSubmittedAt() != null ? savedResult.getSubmittedAt().toString() : null);
        response.setCourse(savedResult.getCourse() != null ? CourseDto.fromEntity(savedResult.getCourse()) : null);
        response.setDetails(getResultDetailsByResultId(savedResult.getId()));

        // 5. Gán trạng thái khóa học
        String courseStatus = enrollmentOpt.map(e -> e.getStatus().name()).orElse(null);
        response.setCourseStatus(courseStatus);

        return response;
    }

    @Override
    public List<CourseQuizResultDto> getResultDtosByUserId(Long userId) {
        return courseQuizResultRepository.findByUserId(userId).stream().map(result -> {
            CourseQuizResultDto dto = new CourseQuizResultDto();
            dto.setId(result.getId());
            dto.setScore(result.getScore());
            dto.setTotalQuestions(result.getTotalQuestions());
            if (result.getCourse() != null) {
                dto.setCourseId(result.getCourse().getId());
                dto.setCourseName(result.getCourse().getName());
            }
            dto.setSubmittedAt(result.getSubmittedAt() != null ? result.getSubmittedAt().toString() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CourseQuizResultDetailDto> getResultDetailsByResultId(Long resultId) {
        return courseQuizResultDetailRepository.findAllByQuizResult_Id(resultId)
                .stream().map(detail -> {
                    CourseQuizResultDetailDto dto = new CourseQuizResultDetailDto();
                    dto.setQuestion(detail.getQuestion());
                    dto.setOptions(detail.getOptions());
                    dto.setCorrectAnswer(detail.getCorrectAnswer());
                    dto.setStudentAnswer(detail.getStudentAnswer());
                    dto.setCorrect(detail.isCorrect());
                    return dto;
                }).collect(Collectors.toList());
    }

    // Không cần override các method không dùng
    @Override
    public CourseQuizResult create(CourseQuizResult result) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<CourseQuizResultDetailDto> getMyResultDetails(Long userId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void submitQuiz(QuizSubmitRequest request, User user) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
