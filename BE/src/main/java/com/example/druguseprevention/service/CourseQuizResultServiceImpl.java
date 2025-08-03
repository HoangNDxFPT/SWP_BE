package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.*;
import com.example.druguseprevention.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tạo constructor tự động cho các final fields
public class CourseQuizResultServiceImpl implements CourseQuizResultService {

    // Inject các repository và object mapper
    private final CourseQuizResultDetailRepository courseQuizResultDetailRepository;
    private final CourseQuizResultRepository courseQuizResultRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Lấy tất cả kết quả quiz
    @Override
    public List<CourseQuizResult> findAll() {
        return courseQuizResultRepository.findAll();
    }

    // Tìm kết quả quiz theo ID
    @Override
    public CourseQuizResult findById(Long id) {
        return courseQuizResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    // Cập nhật kết quả quiz
    @Override
    public CourseQuizResult update(Long id, CourseQuizResult updatedResult) {
        CourseQuizResult existing = findById(id);
        existing.setScore(updatedResult.getScore());
        existing.setTotalQuestions(updatedResult.getTotalQuestions());
        existing.setCourse(updatedResult.getCourse());
        existing.setUser(updatedResult.getUser());
        return courseQuizResultRepository.save(existing);
    }

    // Xoá kết quả theo ID
    @Override
    public void delete(Long id) {
        courseQuizResultRepository.deleteById(id);
    }

    // Kiểm tra xem user có phải chủ nhân của kết quả này không
    @Override
    public boolean isOwner(Long resultId, Long userId) {
        return courseQuizResultRepository.findById(resultId)
                .map(result -> result.getUser().getId().equals(userId))
                .orElse(false);
    }

    // Lấy danh sách kết quả của 1 user
    @Override
    public List<CourseQuizResult> findByUserId(Long userId) {
        return courseQuizResultRepository.findByUserId(userId);
    }

    // Nộp bài quiz và trả về kết quả đầy đủ
    @Override
    public CourseQuizResultFullResponse submitQuizAndReturn(QuizSubmitRequest request, User user) {
        // Tạo bản ghi kết quả
        CourseQuizResult result = new CourseQuizResult();
        result.setUser(user);
        result.setCourse(
                request.getCourseId() != null
                        ? courseRepository.findById(request.getCourseId()).orElseThrow()
                        : null
        );
        result.setScore((int) request.getScore());
        result.setTotalQuestions(request.getAnswers().size());

        CourseQuizResult savedResult = courseQuizResultRepository.save(result);

        // Lưu từng câu trả lời chi tiết
        for (QuizAnswerDto dto : request.getAnswers()) {
            CourseQuizResultDetail detail = new CourseQuizResultDetail();
            detail.setQuestion(dto.getQuestion());
            try {
                detail.setOptions(objectMapper.writeValueAsString(dto.getOptions()));
            } catch (Exception e) {
                detail.setOptions("[]");
                e.printStackTrace();
            }
            detail.setCorrectAnswer(dto.getCorrectAnswer());
            detail.setStudentAnswer(dto.getStudentAnswer());
            detail.setCorrect(dto.getCorrectAnswer().equals(dto.getStudentAnswer()));
            detail.setQuizResult(savedResult);
            courseQuizResultDetailRepository.save(detail);
        }

        // Nếu điểm >= 80%, cập nhật trạng thái khoá học thành "Completed"
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByMemberAndCourse(user, savedResult.getCourse());
        if (savedResult.getScore() >= 0.8 * savedResult.getTotalQuestions()) {
            enrollmentOpt.ifPresent(enrollment -> {
                enrollment.setStatus(Enrollment.Status.Completed);
                enrollmentRepository.save(enrollment);
            });
        }

        // Trả về kết quả đầy đủ
        CourseQuizResultFullResponse response = new CourseQuizResultFullResponse();
        response.setId(savedResult.getId());
        response.setScore(savedResult.getScore());
        response.setTotalQuestions(savedResult.getTotalQuestions());
        response.setSubmittedAt(savedResult.getSubmittedAt() != null ? savedResult.getSubmittedAt().toString() : null);
        response.setCourse(savedResult.getCourse() != null ? CourseDto.fromEntity(savedResult.getCourse()) : null);
        response.setDetails(getResultDetailsByResultId(savedResult.getId()));
        response.setCourseStatus(enrollmentOpt.map(e -> e.getStatus().name()).orElse(null));

        return response;
    }

    // Lấy danh sách kết quả dưới dạng DTO (cho 1 user)
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
            } else {
                dto.setCourseId(null);
                dto.setCourseName("Không xác định");
            }
            dto.setSubmittedAt(result.getSubmittedAt() != null ? result.getSubmittedAt().toString() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    // Lấy danh sách chi tiết kết quả quiz (câu hỏi, đáp án, lựa chọn,...)
    @Override
    public List<CourseQuizResultDetailDto> getResultDetailsByResultId(Long resultId) {
        return courseQuizResultDetailRepository.findAllByQuizResult_Id(resultId)
                .stream()
                .map(detail -> {
                    CourseQuizResultDetailDto dto = new CourseQuizResultDetailDto();
                    dto.setQuestion(detail.getQuestion());

                    String optionsRaw = detail.getOptions();
                    try {
                        if (optionsRaw != null && optionsRaw.trim().startsWith("[") && optionsRaw.trim().endsWith("]")) {
                            dto.setOptions(objectMapper.readValue(optionsRaw, new TypeReference<List<String>>() {}));
                        } else if (optionsRaw != null) {
                            dto.setOptions(Collections.singletonList(optionsRaw));
                        } else {
                            dto.setOptions(Collections.emptyList());
                        }
                    } catch (Exception e) {
                        dto.setOptions(Collections.emptyList());
                        e.printStackTrace();
                    }

                    dto.setCorrectAnswer(detail.getCorrectAnswer());
                    dto.setStudentAnswer(detail.getStudentAnswer());
                    dto.setCorrect(detail.isCorrect());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Không hỗ trợ tạo kết quả quiz bằng tay
    @Override
    public CourseQuizResult create(CourseQuizResult result) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // Lấy chi tiết kết quả quiz gần nhất của user
    @Override
    public List<CourseQuizResultDetailDto> getMyResultDetails(Long userId) {
        return courseQuizResultRepository.findTopByUser_IdOrderBySubmittedAtDesc(userId)
                .map(result -> getResultDetailsByResultId(result.getId()))
                .orElse(Collections.emptyList());
    }

    // Nộp quiz (dạng không cần trả response)
    @Override
    public void submitQuiz(QuizSubmitRequest request, User user) {
        submitQuizAndReturn(request, user);
    }

    // Lấy tất cả kết quả quiz dưới dạng DTO (cho admin)
    @Override
    public List<CourseQuizResultDto> getAllResultDtos() {
        return courseQuizResultRepository.findAll().stream().map(result -> {
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
}
