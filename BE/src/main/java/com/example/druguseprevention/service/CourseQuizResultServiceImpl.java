package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizResultDetailDto;
import com.example.druguseprevention.dto.CourseQuizResultDto;
import com.example.druguseprevention.dto.QuizAnswerDto;
import com.example.druguseprevention.dto.QuizSubmitRequest;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.CourseQuizResultDetail;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseQuizResultDetailRepository;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import com.example.druguseprevention.repository.CourseRepository;
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
    public List<CourseQuizResult> findByUserId(Long userId) {
        return courseQuizResultRepository.findByUserId(userId);
    }

    @Override
    public List<CourseQuizResultDetailDto> getMyResultDetails(Long userId) {
        List<CourseQuizResultDetail> allDetails = courseQuizResultDetailRepository.findAll();
        return allDetails.stream()
                .filter(detail -> detail.getQuizResult().getUser().getId().equals(userId))
                .map(detail -> {
                    CourseQuizResultDetailDto dto = new CourseQuizResultDetailDto();
                    dto.setQuestion(detail.getQuestion());
                    dto.setOptions(detail.getOptions());
                    dto.setCorrectAnswer(detail.getCorrectAnswer());
                    dto.setStudentAnswer(detail.getStudentAnswer());
                    dto.setCorrect(detail.isCorrect());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void submitQuiz(QuizSubmitRequest request, User user) {
        CourseQuizResult result = new CourseQuizResult();
        result.setUser(user);
        result.setCourse(request.getCourseId() != null ? courseRepository.findById(request.getCourseId()).orElseThrow() : null); // sửa tùy vào logic bạn có
        result.setScore((int) request.getScore());
        result.setTotalQuestions(request.getAnswers().size());
        courseQuizResultRepository.save(result);

        for (QuizAnswerDto dto : request.getAnswers()) {
            CourseQuizResultDetail detail = new CourseQuizResultDetail();
            detail.setQuestion(dto.getQuestion());
            detail.setOptions(dto.getOptions());
            detail.setCorrectAnswer(dto.getCorrectAnswer());
            detail.setStudentAnswer(dto.getStudentAnswer());
            detail.setCorrect(dto.getCorrectAnswer().equals(dto.getStudentAnswer()));
            detail.setQuizResult(result); //  Gán quizResult

            courseQuizResultDetailRepository.save(detail);
        }
    }
        public List<CourseQuizResultDto> getResultDtosByUserId(Long userId) {
            return courseQuizResultRepository.findByUserId(userId).stream().map(result -> {
                CourseQuizResultDto dto = new CourseQuizResultDto();
                dto.setId(result.getId());
                dto.setScore(result.getScore());
                dto.setTotalQuestions(result.getTotalQuestions());
                dto.setCourseName(result.getCourse() != null ? result.getCourse().getName() : null);
                dto.setSubmittedAt(result.getSubmittedAt() != null ? result.getSubmittedAt().toString() : null);
                return dto;
            }).collect(Collectors.toList());
        }

    }
