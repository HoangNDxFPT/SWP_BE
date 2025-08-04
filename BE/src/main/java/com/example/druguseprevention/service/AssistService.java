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
import java.util.stream.Collectors;

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

        // 2. Xử lý từng substance riêng biệt
        List<AssistResultResponse.SubstanceResult> substanceResults = new ArrayList<>();
        List<UserAssessmentAnswer> allUserAnswers = new ArrayList<>();
        RiskLevel highestRiskLevel = RiskLevel.LOW;
        int totalOverallScore = 0;
        AssistResultResponse.InjectionResult injectionResult = null; // Kết quả injection từ Q8

        for (AssistSubmissionRequest.SubstanceAssessment substanceAssessment : request.getSubstanceAssessments()) {
            // Lấy thông tin substance
            Substance substance = substanceRepository.findByIdAndIsDeletedFalse(substanceAssessment.getSubstanceId())
                .orElseThrow(() -> new BadRequestException("Substance not found: " + substanceAssessment.getSubstanceId()));

            // Lấy questions Q2-Q8 cho substance này
            List<AssessmentQuestion> questions = assessmentQuestionRepository
                .findByAssessmentTypeAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST)
                .stream()
                .filter(q -> q.getQuestionOrder() >= 2 && q.getQuestionOrder() <= 8)
                .toList();

            // Kiểm tra Q2 để xác định có sử dụng trong 3 tháng qua không
            boolean usedInPast3Months = true; // Mặc định là có sử dụng
            if (!substanceAssessment.getAnswerIds().isEmpty() && !questions.isEmpty()) {
                AssessmentQuestion q2 = questions.get(0); // Q2 là câu đầu tiên
                if (q2.getQuestionOrder() == 2) {
                    Long q2AnswerId = substanceAssessment.getAnswerIds().get(0);
                    AssessmentAnswer q2Answer = assessmentAnswerRepository.findById(q2AnswerId)
                        .orElseThrow(() -> new BadRequestException("Answer not found: " + q2AnswerId));

                    // Nếu điểm = 0 thì là "Never" = không sử dụng trong 3 tháng qua
                    usedInPast3Months = q2Answer.getScore() > 0;
                }
            }

            // Tính điểm cho substance này
            int substanceScore = 0;
            List<AssistResultResponse.QuestionAnswer> questionAnswers = new ArrayList<>();

            int answerIndex = 0; // Track current position in answerIds array

            for (int questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
                AssessmentQuestion question = questions.get(questionIndex);

                // Skip Q3, Q4, Q5 nếu Q2 trả lời "Never" (không sử dụng trong 3 tháng qua)
                // Nhưng vẫn làm Q6, Q7, Q8
                if (!usedInPast3Months && (question.getQuestionOrder() >= 3 && question.getQuestionOrder() <= 5)) {
                    continue; // Skip question nhưng không tăng answerIndex
                }

                // Kiểm tra có đủ answer không
                if (answerIndex >= substanceAssessment.getAnswerIds().size()) {
                    break;
                }

                Long answerId = substanceAssessment.getAnswerIds().get(answerIndex);
                answerIndex++; // Tăng answerIndex sau khi sử dụng

                AssessmentAnswer answer = assessmentAnswerRepository.findById(answerId)
                    .orElseThrow(() -> new BadRequestException("Answer not found: " + answerId));

                // Validate answer belongs to question
                if (!answer.getQuestion().getId().equals(question.getId())) {
                    throw new BadRequestException("Answer does not belong to question");
                }

                // Cộng điểm cho substance này (Q8 không cộng điểm vào substance score)
                int answerScore = Optional.ofNullable(answer.getScore()).orElse(0);
                if (question.getQuestionOrder() != 8) {
                    substanceScore += answerScore;
                } else {
                    // Xử lý Q8 injection question - chỉ lấy 1 lần cho toàn bộ assessment
                    if (injectionResult == null) {
                        injectionResult = new AssistResultResponse.InjectionResult();
                        injectionResult.setQuestionText(question.getQuestionText());
                        injectionResult.setAnswerText(answer.getAnswerText());
                        injectionResult.setScore(answerScore);

                        // Đánh giá risk injection
                        if (answerScore > 0) {
                            injectionResult.setRiskAssessment("Injection risk detected - Substance use by injection increases health risks significantly");
                        } else {
                            injectionResult.setRiskAssessment("No injection risk detected");
                        }
                    }
                }

                // Lưu user answer với substance info
                UserAssessmentAnswer userAnswer = new UserAssessmentAnswer();
                userAnswer.setQuestion(question);
                userAnswer.setAnswer(answer);
                userAnswer.setSubstance(substance);
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
        response.setInjectionResult(injectionResult); // Thêm injection result

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
            // Các chất khác dùng thang điểm chuẩnn
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

    // API xem kết quả ASSIST theo ID
    public AssistResultResponse getAssistResult(Long assessmentResultId) {
        User currentUser = assessmentService.getCurrentUser();

        AssessmentResult result = resultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new BadRequestException("Assessment result not found: " + assessmentResultId));

        // Kiểm tra quyền access (chỉ owner hoặc admin/consultant)
        if (!result.getAssessment().getMember().getId().equals(currentUser.getId()) &&
            !currentUser.getRole().name().equals("ADMIN") &&
            !currentUser.getRole().name().equals("CONSULTANT")) {
            throw new BadRequestException("Access denied to this assessment result");
        }

        // Kiểm tra type là ASSIST
        if (!result.getAssessment().getType().equals(AssessmentType.ASSIST)) {
            throw new BadRequestException("This is not an ASSIST assessment result");
        }

        return buildAssistResultResponse(result);
    }



    // Helper method để build AssistResultResponse từ AssessmentResult
    private AssistResultResponse buildAssistResultResponse(AssessmentResult result) {
        // Lấy tất cả user answers cho result này
        List<UserAssessmentAnswer> userAnswers = result.getUserAnswers();

        // Group answers by substance
        Map<Substance, List<UserAssessmentAnswer>> answersBySubstance = userAnswers.stream()
            .filter(ua -> ua.getSubstance() != null)
            .collect(Collectors.groupingBy(UserAssessmentAnswer::getSubstance));

        // Build substance results
        List<AssistResultResponse.SubstanceResult> substanceResults = new ArrayList<>();
        AssistResultResponse.InjectionResult injectionResult = null;

        for (Map.Entry<Substance, List<UserAssessmentAnswer>> entry : answersBySubstance.entrySet()) {
            Substance substance = entry.getKey();
            List<UserAssessmentAnswer> substanceAnswers = entry.getValue();

            // Calculate substance score và build question answers
            int substanceScore = 0;
            List<AssistResultResponse.QuestionAnswer> questionAnswers = new ArrayList<>();

            for (UserAssessmentAnswer userAnswer : substanceAnswers) {
                AssessmentQuestion question = userAnswer.getQuestion();
                AssessmentAnswer answer = userAnswer.getAnswer();

                // Handle Q8 injection question
                if (question.getQuestionOrder() == 8 && injectionResult == null) {
                    injectionResult = new AssistResultResponse.InjectionResult();
                    injectionResult.setQuestionText(question.getQuestionText());
                    injectionResult.setAnswerText(answer.getAnswerText());
                    injectionResult.setScore(Optional.ofNullable(answer.getScore()).orElse(0));

                    if (injectionResult.getScore() > 0) {
                        injectionResult.setRiskAssessment("Injection risk detected - Substance use by injection increases health risks significantly");
                    } else {
                        injectionResult.setRiskAssessment("No injection risk detected");
                    }
                }

                // Add to substance score (exclude Q8)
                int answerScore = Optional.ofNullable(answer.getScore()).orElse(0);
                if (question.getQuestionOrder() != 8) {
                    substanceScore += answerScore;
                }

                // Build question answer
                AssistResultResponse.QuestionAnswer qa = new AssistResultResponse.QuestionAnswer();
                String questionText = question.getQuestionText();
                if (question.getQuestionOrder() != 8) {
                    questionText = questionText.replace("[SUBSTANCE]", substance.getName());
                }
                qa.setQuestionText(questionText);
                qa.setAnswerText(answer.getAnswerText());
                qa.setScore(answerScore);
                questionAnswers.add(qa);
            }

            // Build substance result
            AssistResultResponse.SubstanceResult substanceResult = new AssistResultResponse.SubstanceResult();
            substanceResult.setSubstanceId(substance.getId());
            substanceResult.setSubstanceName(substance.getName());
            substanceResult.setSubstanceDescription(substance.getDescription());
            substanceResult.setScore(substanceScore);
            substanceResult.setRiskLevel(determineSubstanceRiskLevel(substance.getName(), substanceScore));
            substanceResult.setCriteria(getRiskCriteria(substance.getName()));
            substanceResult.setAnswers(questionAnswers);

            substanceResults.add(substanceResult);
        }

        // Get course recommendations
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

        // Build response
        AssistResultResponse response = new AssistResultResponse();
        response.setAssessmentResultId(result.getId());
        response.setAssessmentId(result.getAssessment().getId());
        response.setSubstanceResults(substanceResults);
        response.setOverallRiskLevel(result.getRiskLevel());
        response.setRecommendation(result.getRecommendation().getMessage());
        response.setSubmittedAt(result.getDateTaken());
        response.setInjectionResult(injectionResult);
        response.setRecommendedCourses(courseDTOs);

        return response;
    }
}
