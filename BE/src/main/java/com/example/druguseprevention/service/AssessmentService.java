package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssessmentResultResponse;
import com.example.druguseprevention.dto.AssessmentStartResponse;
import com.example.druguseprevention.dto.AssessmentSubmissionRequest;
import com.example.druguseprevention.entity.*;
import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.enums.RiskLevel;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    private final AssessmentResultRepository resultRepository;

    private final RiskRecommendationRepository riskRecommendationRepository;

    private final UserAssessmentAnswerRepository userAssessmentAnswerRepository;

    private final AssessmentQuestionRepository assessmentQuestionRepository;

    private final AssessmentAnswerRepository assessmentAnswerRepository;

    private final UserRepository userRepository;

    private final RecommendationCourseRepository recommendationCourseRepository;

    private final CourseRepository courseRepository;


    public AssessmentResultResponse convertToResponse(AssessmentResult result) {
        AssessmentResultResponse response = new AssessmentResultResponse();
        response.setAssessmentResultId(result.getId());
        response.setAssessmentId(result.getAssessment().getId());
        response.setAssessmentType(result.getAssessment().getType());
        response.setScore(result.getScore());
        response.setRiskLevel(result.getRiskLevel());
        response.setRecommendation(result.getRecommendation().getMessage());
        response.setSubmittedAt(result.getDateTaken());

        List<AssessmentResultResponse.AnswerDetail> answerDetails = result.getUserAnswers().stream().map(userAnswer -> {
            AssessmentResultResponse.AnswerDetail detail = new AssessmentResultResponse.AnswerDetail();
            detail.setQuestionId(userAnswer.getQuestion().getId());
            detail.setQuestionText(userAnswer.getQuestion().getQuestionText());
            detail.setAnswerId(userAnswer.getAnswer().getId());
            detail.setAnswerText(userAnswer.getAnswer().getAnswerText());
            detail.setScore(userAnswer.getAnswer().getScore());

            // Thêm thông tin substance nếu có
            if (userAnswer.getSubstance() != null) {
                detail.setSubstanceId(userAnswer.getSubstance().getId());
                detail.setSubstanceName(userAnswer.getSubstance().getName());
                detail.setSubstanceDescription(userAnswer.getSubstance().getDescription());
            }

            return detail;
        }).toList();

        response.setAnswers(answerDetails);

        // Nếu là MEDIUM thì thêm danh sách khóa học gợi ý
        if (result.getRiskLevel() == RiskLevel.MEDIUM) {
            List<RecommendationCourse> recCourses = recommendationCourseRepository
                    .findByIdAssessmentResultId(result.getId());

            List<AssessmentResultResponse.CourseDTO> courseDTOs = recCourses.stream().map(rc -> {
                Course course = rc.getCourse();
                AssessmentResultResponse.CourseDTO dto = new AssessmentResultResponse.CourseDTO();
                dto.setId(course.getId());
                dto.setName(course.getName());
                dto.setDescription(course.getDescription());
                dto.setTargetAgeGroup(course.getTargetAgeGroup().name());
                return dto;
            }).toList();

            response.setRecommendedCourses(courseDTOs);
        } else {
            // nếu là HIGH hoặc LOW thì sẽ trả về chuỗi rỗng
            response.setRecommendedCourses(Collections.emptyList());
        }


        return response;
    }
//    Lấy danh sách câu hỏi đánh giá theo loại (ASSIST hoặc CRAFFT)
    public List<AssessmentQuestion> getQuestionsByType(AssessmentType type) {
        return assessmentQuestionRepository.findByAssessmentTypeOrderByQuestionOrder(type);
    }
// Lấy thông tin user hiện tại khi đăng nhập
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }




// Bắt đầu làm bài đánh giá
public AssessmentStartResponse startAssessment(AssessmentType type) {


    List<AssessmentQuestion> questions = assessmentQuestionRepository.findByAssessmentTypeAndIsDeletedFalseOrderByQuestionOrder(type);


    List<AssessmentStartResponse.QuestionDTO> questionDtos = questions.stream().map(question -> {
        AssessmentStartResponse.QuestionDTO qDto = new AssessmentStartResponse.QuestionDTO();
        qDto.setId(question.getId());
        qDto.setQuestionText(question.getQuestionText());

        List<AssessmentAnswer> answers = assessmentAnswerRepository.findByQuestionIdAndIsDeletedFalse(question.getId());
        List<AssessmentStartResponse.AnswerDTO> answerDtos = answers.stream().map(a -> {
            AssessmentStartResponse.AnswerDTO aDto = new AssessmentStartResponse.AnswerDTO();
            aDto.setId(a.getId());
            aDto.setText(a.getAnswerText());
//            aDto.setScore(a.getScore()); dùng để hiển thị điểm đáp án trên bài làm
            return aDto;
        }).toList();

        qDto.setAnswers(answerDtos);
        return qDto;
    }).toList();

    AssessmentStartResponse response = new AssessmentStartResponse();
    response.setType(type.name());
    response.setMessage("Assessment started");
    response.setQuestions(questionDtos);
    return response;
}
// Lấy bài đánh giá gần nhất của người dùng hiện tại
    public Assessment getMyLatestAssessment() {
        return assessmentRepository.findFirstByMemberOrderByCreatedAtDesc(getCurrentUser())
                .orElseThrow(() -> new BadRequestException("No assessment found"));
    }

//Xem bài đánh giá cụ thể theo ID
    public Assessment getAssessmentById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Assessment not found"));
    }

//Lịch sử đánh giá của người dùng hiện tại
    public List<Assessment> getMyHistory() {
        return assessmentRepository.findByMember(getCurrentUser());
    }

// Xem toàn bộ lịch sử đánh giá của mọi người
    public List<Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }




//    Xử lý khi người dùng submit bài đánh giá
    @Transactional // dùng để khi mà hàm này chạy bị lỗi ở 1 đoạn nào đó thì nó sẽ ko lưu xuống DB
    public AssessmentResultResponse  submit(AssessmentType type, List<AssessmentSubmissionRequest> assessmentSubmissionRequests) {

        User user = getCurrentUser();

        // 1. Tạo mới assessment
        Assessment assessment = new Assessment();
        assessment.setType(type);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setMember(user);
        assessment.setSubmitted(true); // đánh dấu đã nộp
        assessment = assessmentRepository.save(assessment);


        // 2. Duyệt từng câu trả lời, tính tổng điểm và lưu tạm vào danh sách
        int totalScore = 0;
        List<UserAssessmentAnswer> userAssessmentAnswers = new ArrayList<>();

        for (AssessmentSubmissionRequest request : assessmentSubmissionRequests) {
            AssessmentQuestion question = assessmentQuestionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new BadRequestException("Question not found: " + request.getQuestionId()));

            AssessmentAnswer answer = assessmentAnswerRepository.findById(request.getAnswerId())
                    .orElseThrow(() -> new BadRequestException("Answer not found: " + request.getAnswerId()));


            if (question == null) {
                throw new BadRequestException("Question not found: " + request.getQuestionId());
            }
            if (answer == null) {
                throw new BadRequestException("Answer not found: " + request.getAnswerId());
            }
            // Đảm bảo answer thuộc về question
            if (!answer.getQuestion().getId().equals(question.getId())) {
                throw new IllegalArgumentException("Answer does not belong to the question");
            }

            totalScore += Optional.ofNullable(answer.getScore()).orElse(0);

            UserAssessmentAnswer userAnswer = new UserAssessmentAnswer();
            userAnswer.setQuestion(question);
            userAnswer.setAnswer(answer);
            userAnswer.setSelectedAt(LocalDateTime.now());
            userAssessmentAnswers.add(userAnswer);
        }


        // 3. Xác định mức độ rủi ro dựa trên tổng điểm
        RiskLevel riskLevel = determineRiskLevel(assessment.getType(), totalScore);


        // 4. Tìm đề xuất rủi ro tương ứng
        RiskRecommendation recommendation = riskRecommendationRepository.findByRiskLevel(riskLevel)
                .orElseThrow(() -> new BadRequestException("Missing risk config for level: " + riskLevel));


        // 5. Tạo bản ghi AssessmentResult
        AssessmentResult result = new AssessmentResult();
        result.setAssessment(assessment);
        result.setScore(totalScore);
        result.setRiskLevel(riskLevel);
        result.setRecommendation(recommendation);
        result.setDateTaken(LocalDateTime.now());
        result.setUserAnswers(userAssessmentAnswers);

        result = resultRepository.save(result);

        // 6. Gán kết quả cho từng câu trả lời của người dùng
        for (UserAssessmentAnswer answer : userAssessmentAnswers) {
            answer.setAssessmentResult(result);
        }
        userAssessmentAnswerRepository.saveAll(userAssessmentAnswers);


        // 7. Nếu rủi ro mức trung bình -> đề xuất khóa học phù hợp độ tuổi
        if (riskLevel == RiskLevel.MEDIUM) {
            int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
            Course.TargetAgeGroup group;
            if (age < 18) {
                group = Course.TargetAgeGroup.Teenagers;
            } else {
                group = Course.TargetAgeGroup.Adults;
            }

            List<Course> courses = courseRepository.findByTargetAgeGroup(group);

            // Lưu các khóa học đề xuất theo độ tuổi vào recommendation
            for (Course course : courses) {

                RecommendationCourse recommendationCourse = new RecommendationCourse();

                // Set composite key
                RecommendationCourseId recommendationCourseId = new RecommendationCourseId(result.getId(), course.getId());
                recommendationCourse.setId(recommendationCourseId);

                // Set entity references (MapsId yêu cầu)
                recommendationCourse.setAssessmentResult(result); // Gắn theo kết quả
                recommendationCourse.setCourse(course);
                recommendationCourseRepository.save(recommendationCourse);
            }
        }

            return convertToResponse(result);
        }

    // dùng để tính điểm riêng biệt từng bài đánh giá theo loại
    public RiskLevel determineRiskLevel(AssessmentType type, int totalScore) {
        switch (type) {
            case CRAFFT:
                if (totalScore <= 1) {
                    return RiskLevel.LOW;
                } else if (totalScore <= 3) {
                    return RiskLevel.MEDIUM;
                } else {
                    return RiskLevel.HIGH;
                }

            default:
                throw new IllegalArgumentException("Unsupported assessment type for risk evaluation: " + type + ". Use specialized service instead.");
        }
    }

}
