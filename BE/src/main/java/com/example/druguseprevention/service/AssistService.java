package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssistResultResponse;
import com.example.druguseprevention.dto.AssistSubmissionRequest;
import com.example.druguseprevention.dto.AssistStartResponse;
import com.example.druguseprevention.entity.*;
import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.enums.RiskLevel;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssistService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentResultRepository resultRepository;
    private final RiskRecommendationRepository riskRecommendationRepository;
    private final UserAssessmentAnswerRepository userAssessmentAnswerRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final AssessmentAnswerRepository assessmentAnswerRepository;
    private final UserRepository userRepository;
    private final RecommendationCourseRepository recommendationCourseRepository;
    private final CourseRepository courseRepository;
    private final AssessmentService assessmentService;
    private final SubstanceRepository substanceRepository;

    @Transactional
    public AssistResultResponse submitAssistAssessment(AssistSubmissionRequest request) {
        User user = assessmentService.getCurrentUser();

        // 1. Tạo assessment
        Assessment assessment = new Assessment();
        assessment.setType(AssessmentType.ASSIST);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setMember(user);
        assessment.setSubmitted(true);
        assessment = assessmentRepository.save(assessment);

        // 2. Xử lý từng substance riêng biệt (Q2-Q8 cho mỗi substance)
        List<AssistResultResponse.SubstanceResult> substanceResults = new ArrayList<>();
        List<UserAssessmentAnswer> allUserAnswers = new ArrayList<>();
        RiskLevel highestRiskLevel = RiskLevel.LOW;
        int totalOverallScore = 0;

        for (AssistSubmissionRequest.SubstanceAssessment substanceAssessment : request.getSubstanceAssessments()) {
            // Lấy thông tin substance
            Substance substance = substanceRepository.findByIdAndIsDeletedFalse(substanceAssessment.getSubstanceId())
                .orElseThrow(() -> new BadRequestException("Substance not found: " + substanceAssessment.getSubstanceId()));

            // Lấy questions Q2-Q8 cho substance này (bao gồm cả injection)
            List<AssessmentQuestion> questions = assessmentQuestionRepository
                .findByAssessmentTypeAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST)
                .stream()
                .filter(q -> q.getQuestionOrder() >= 2 && q.getQuestionOrder() <= 8)
                .toList();

            // Tính điểm cho substance này
            int substanceScore = 0;
            List<AssistResultResponse.QuestionAnswer> questionAnswers = new ArrayList<>();

            for (int i = 0; i < substanceAssessment.getAnswerIds().size() && i < questions.size(); i++) {
                AssessmentQuestion question = questions.get(i);
                Long answerId = substanceAssessment.getAnswerIds().get(i);

                AssessmentAnswer answer = assessmentAnswerRepository.findById(answerId)
                    .orElseThrow(() -> new BadRequestException("Answer not found: " + answerId));

                // Validate answer belongs to question
                if (!answer.getQuestion().getId().equals(question.getId())) {
                    throw new BadRequestException("Answer does not belong to question");
                }

                // Cộng điểm cho substance này
                int answerScore = Optional.ofNullable(answer.getScore()).orElse(0);
                substanceScore += answerScore;

                // Lưu user answer với substance info (Q8 injection cũng gắn với substance)
                UserAssessmentAnswer userAnswer = new UserAssessmentAnswer();
                userAnswer.setQuestion(question);
                userAnswer.setAnswer(answer);
                userAnswer.setSubstance(substance); // Cả Q8 cũng gắn với substance
                userAnswer.setSelectedAt(LocalDateTime.now());
                allUserAnswers.add(userAnswer);

                // Tạo response detail
                AssistResultResponse.QuestionAnswer qa = new AssistResultResponse.QuestionAnswer();
                String questionText = question.getQuestionText();
                // Chỉ thay thế [SUBSTANCE] cho Q2-Q7, Q8 giữ nguyên
                if (question.getQuestionOrder() != 8) {
                    questionText = questionText.replace("[SUBSTANCE]", substance.getName());
                }
                qa.setQuestionText(questionText);
                qa.setAnswerText(answer.getAnswerText());
                qa.setScore(answerScore);
                questionAnswers.add(qa);
            }

            // Tính risk level cho substance này
            RiskLevel substanceRiskLevel = determineSubstanceRiskLevel(substance.getName(), substanceScore);

            // Cập nhật highest risk level
            if (substanceRiskLevel.ordinal() > highestRiskLevel.ordinal()) {
                highestRiskLevel = substanceRiskLevel;
            }

            // Cộng vào tổng điểm overall
            totalOverallScore += substanceScore;

            // Tạo substance result
            AssistResultResponse.SubstanceResult substanceResult = new AssistResultResponse.SubstanceResult();
            substanceResult.setSubstanceId(substance.getId());
            substanceResult.setSubstanceName(substance.getName());
            substanceResult.setSubstanceDescription(substance.getDescription());
            substanceResult.setScore(substanceScore);
            substanceResult.setRiskLevel(substanceRiskLevel);
            substanceResult.setCriteria(getRiskCriteria(substance.getName()));
            substanceResult.setAnswers(questionAnswers);

            substanceResults.add(substanceResult);
        }

        // 3. Risk level overall dựa trên highest risk từ các substances
        final RiskLevel finalHighestRiskLevel = highestRiskLevel;
        RiskRecommendation recommendation = riskRecommendationRepository.findByRiskLevel(finalHighestRiskLevel)
                .orElseThrow(() -> new BadRequestException("Missing risk config for level: " + finalHighestRiskLevel));

        // 4. Tạo AssessmentResult
        AssessmentResult result = new AssessmentResult();
        result.setAssessment(assessment);
        result.setScore(totalOverallScore); // Tổng điểm của tất cả substances
        result.setRiskLevel(finalHighestRiskLevel);
        result.setRecommendation(recommendation);
        result.setDateTaken(LocalDateTime.now());
        result.setUserAnswers(allUserAnswers);
        result = resultRepository.save(result);

        // 5. Gán assessment result cho user answers
        for (UserAssessmentAnswer answer : allUserAnswers) {
            answer.setAssessmentResult(result);
        }
        userAssessmentAnswerRepository.saveAll(allUserAnswers);

        // 6. Course recommendations nếu có risk MEDIUM
        if (finalHighestRiskLevel == RiskLevel.MEDIUM) {
            addCourseRecommendations(user, result);
        }

        // 7. Tạo response
        AssistResultResponse response = new AssistResultResponse();
        response.setAssessmentResultId(result.getId());
        response.setAssessmentId(assessment.getId());
        response.setSubstanceResults(substanceResults);
        response.setOverallRiskLevel(finalHighestRiskLevel);
        response.setRecommendation(recommendation.getMessage());
        response.setSubmittedAt(result.getDateTaken());

        if (finalHighestRiskLevel == RiskLevel.MEDIUM) {
            List<RecommendationCourse> recCourses = recommendationCourseRepository
                    .findByIdAssessmentResultId(result.getId());
            List<AssistResultResponse.CourseDTO> courseDTOs = recCourses.stream().map(rc -> {
                Course course = rc.getCourse();
                AssistResultResponse.CourseDTO dto = new AssistResultResponse.CourseDTO();
                dto.setId(course.getId());
                dto.setName(course.getName());
                dto.setDescription(course.getDescription());
                dto.setTargetAgeGroup(course.getTargetAgeGroup().name());
                return dto;
            }).toList();
            response.setRecommendedCourses(courseDTOs);
        } else {
            response.setRecommendedCourses(Collections.emptyList());
        }

        return response;
    }

    public AssistStartResponse startAssistAssessment() {
        AssistStartResponse response = new AssistStartResponse();
        response.setMessage("ASSIST Assessment started");

        // 1. Lấy danh sách substances
        List<Substance> substances = substanceRepository.findByIsDeletedFalse();
        List<AssistStartResponse.SubstanceOption> substanceOptions = substances.stream().map(substance -> {
            AssistStartResponse.SubstanceOption option = new AssistStartResponse.SubstanceOption();
            option.setId(substance.getId());
            option.setName(substance.getName());
            option.setDescription(substance.getDescription());
            return option;
        }).toList();
        response.setSubstances(substanceOptions);

        // 2. Lấy template questions Q2-Q7 (không bao gồm Q1 và Q8)
        List<AssessmentQuestion> templateQuestions = assessmentQuestionRepository
            .findByAssessmentTypeAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST)
            .stream()
            .filter(q -> q.getQuestionOrder() >= 2 && q.getQuestionOrder() <= 7)
            .toList();

        List<AssistStartResponse.TemplateQuestion> templateQuestionDTOs = templateQuestions.stream().map(question -> {
            AssistStartResponse.TemplateQuestion template = new AssistStartResponse.TemplateQuestion();
            template.setQuestionOrder(question.getQuestionOrder());
            template.setQuestionTemplate(question.getQuestionText());

            // Lấy answer options cho template question
            List<AssessmentAnswer> answers = assessmentAnswerRepository.findByQuestionIdAndIsDeletedFalse(question.getId());
            List<AssistStartResponse.AnswerOption> answerOptions = answers.stream().map(answer -> {
                AssistStartResponse.AnswerOption option = new AssistStartResponse.AnswerOption();
                option.setId(answer.getId());
                option.setText(answer.getAnswerText());
                return option;
            }).toList();
            template.setAnswerOptions(answerOptions);
            return template;
        }).toList();
        response.setTemplateQuestions(templateQuestionDTOs);

        // 3. Lấy injection question (Q8)
        List<AssessmentQuestion> injectionQuestions = assessmentQuestionRepository
            .findByAssessmentTypeAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST)
            .stream()
            .filter(q -> q.getQuestionOrder() == 8)
            .toList();

        if (!injectionQuestions.isEmpty()) {
            AssessmentQuestion injectionQ = injectionQuestions.get(0);
            AssistStartResponse.InjectionQuestion injectionQuestion = new AssistStartResponse.InjectionQuestion();
            injectionQuestion.setQuestionId(injectionQ.getId());
            injectionQuestion.setQuestionText(injectionQ.getQuestionText());

            List<AssessmentAnswer> injectionAnswers = assessmentAnswerRepository.findByQuestionIdAndIsDeletedFalse(injectionQ.getId());
            List<AssistStartResponse.AnswerOption> injectionOptions = injectionAnswers.stream().map(answer -> {
                AssistStartResponse.AnswerOption option = new AssistStartResponse.AnswerOption();
                option.setId(answer.getId());
                option.setText(answer.getAnswerText());
                return option;
            }).toList();
            injectionQuestion.setAnswerOptions(injectionOptions);
            response.setInjectionQuestion(injectionQuestion);
        }

        return response;
    }

    private RiskLevel determineSubstanceRiskLevel(String substanceName, int score) {
        // Alcohol có thang điểm khác với các chất khác
        if (substanceName.toLowerCase().contains("alcohol")) {
            if (score <= 10) return RiskLevel.LOW;
            else if (score <= 26) return RiskLevel.MEDIUM;
            else return RiskLevel.HIGH;
        } else {
            // Các chất khác dùng thang điểm chuẩn
            if (score <= 3) return RiskLevel.LOW;
            else if (score <= 26) return RiskLevel.MEDIUM;
            else return RiskLevel.HIGH;
        }
    }

    private String getRiskCriteria(String substanceName) {
        if (substanceName.toLowerCase().contains("alcohol")) {
            return "1-10 Low, 11-26 Moderate, 27+ High";
        } else {
            return "1-3 Low, 4-26 Moderate, 27+ High";
        }
    }


    private void addCourseRecommendations(User user, AssessmentResult result) {
        int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
        Course.TargetAgeGroup group = age < 18 ? Course.TargetAgeGroup.Teenagers : Course.TargetAgeGroup.Adults;

        List<Course> courses = courseRepository.findByTargetAgeGroup(group);
        for (Course course : courses) {
            RecommendationCourse recommendationCourse = new RecommendationCourse();
            RecommendationCourseId id = new RecommendationCourseId(result.getId(), course.getId());
            recommendationCourse.setId(id);
            recommendationCourse.setAssessmentResult(result);
            recommendationCourse.setCourse(course);
            recommendationCourseRepository.save(recommendationCourse);
        }
    }
}
