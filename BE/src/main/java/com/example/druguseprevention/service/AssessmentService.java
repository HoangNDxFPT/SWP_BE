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
    @Autowired
    AssessmentRepository assessmentRepository;
    @Autowired
    AssessmentResultRepository resultRepository;
    @Autowired
    RiskRecommendationRepository riskRecommendationRepository;
    @Autowired
    UserAssessmentAnswerRepository userAssessmentAnswerRepository;
    @Autowired
    AssessmentQuestionRepository assessmentQuestionRepository;
    @Autowired
    AssessmentAnswerRepository assessmentAnswerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RecommendationCourseRepository recommendationCourseRepository;
    @Autowired
    CourseRepository courseRepository;


    public AssessmentResultResponse convertToResponse(AssessmentResult result) {
        AssessmentResultResponse response = new AssessmentResultResponse();
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
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }




// Bắt đầu làm bài đánh giá
public AssessmentStartResponse startAssessment(AssessmentType type) {
    User user = getCurrentUser();

    Assessment assessment = new Assessment();
    assessment.setType(type);
    assessment.setCreatedAt(LocalDateTime.now());
    assessment.setMember(user);
    assessment = assessmentRepository.save(assessment);

    List<AssessmentQuestion> questions = assessmentQuestionRepository.findByAssessmentTypeOrderByQuestionOrder(type);

    List<AssessmentStartResponse.QuestionDTO> questionDtos = questions.stream().map(q -> {
        AssessmentStartResponse.QuestionDTO qDto = new AssessmentStartResponse.QuestionDTO();
        qDto.setId(q.getId());
        qDto.setQuestionText(q.getQuestionText());

        List<AssessmentAnswer> answers = assessmentAnswerRepository.findByQuestionId(q.getId());
        List<AssessmentStartResponse.AnswerDTO> answerDtos = answers.stream().map(a -> {
            AssessmentStartResponse.AnswerDTO aDto = new AssessmentStartResponse.AnswerDTO();
            aDto.setId(a.getId());
            aDto.setText(a.getAnswerText());
//            aDto.setScore(a.getScore());
            return aDto;
        }).toList();

        qDto.setAnswers(answerDtos);
        return qDto;
    }).toList();

    AssessmentStartResponse response = new AssessmentStartResponse();
    response.setAssessmentId(assessment.getId());
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
    public AssessmentResultResponse  submit(Long assessmentId, List<AssessmentSubmissionRequest> assessmentSubmissionRequests) {
        // 1. Lấy bài đánh giá đã được tạo ở bước start
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new BadRequestException("Assessment not found: " + assessmentId));

        // chỉ đc submit 1 lần
        if (assessment.isSubmitted()) {
            throw new BadRequestException("This assessment has already been submitted.");
        }

        User user = assessment.getMember();

        // 2. Chuẩn bị danh sách câu hỏi và câu trả lời
        List<Long> questionIds = assessmentSubmissionRequests.stream()
                .map(AssessmentSubmissionRequest::getQuestionId)
                .toList();

        List<Long> answerIds = assessmentSubmissionRequests.stream()
                .map(AssessmentSubmissionRequest::getAnswerId)
                .toList();

        Map<Long, AssessmentQuestion> questionsMap = assessmentQuestionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(AssessmentQuestion::getId, Function.identity()));

        Map<Long, AssessmentAnswer> answersMap = assessmentAnswerRepository.findAllById(answerIds).stream()
                .collect(Collectors.toMap(AssessmentAnswer::getId, Function.identity()));

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
        RiskLevel riskLevel;
        if (totalScore < 10) {
            riskLevel = RiskLevel.LOW;
        } else if (totalScore < 20) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.HIGH;
        }

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

        // 7. Đánh dấu bài đã được nộp
        assessment.setSubmitted(true);
        assessmentRepository.save(assessment);

        // 8. Nếu rủi ro mức trung bình -> đề xuất khóa học phù hợp độ tuổi
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

}
