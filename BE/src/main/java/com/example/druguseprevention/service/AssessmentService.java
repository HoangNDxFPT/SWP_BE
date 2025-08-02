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
    @Autowired
    SubstanceRepository substanceRepository;


    public AssessmentResultResponse convertToResponse(AssessmentResult result) {
        AssessmentResultResponse response = new AssessmentResultResponse();
        response.setAssessmentResultId(result.getId());
        response.setAssessmentId(result.getAssessment().getId());
        response.setAssessmentType(result.getAssessment().getType());
        response.setTotalScore(result.getScore()); // Điểm tổng (deprecated cho ASSIST)
        response.setOverallRiskLevel(result.getRiskLevel()); // Risk level tổng thể (deprecated cho ASSIST)
        response.setRecommendation(result.getRecommendation().getMessage());
        response.setSubmittedAt(result.getDateTaken());

        List<AssessmentResultResponse.AnswerDetail> answerDetails = result.getUserAnswers().stream().map(userAnswer -> {
            AssessmentResultResponse.AnswerDetail detail = new AssessmentResultResponse.AnswerDetail();
            detail.setQuestionId(userAnswer.getQuestion().getId());
            detail.setQuestionText(userAnswer.getQuestion().getQuestionText());
            detail.setAnswerId(userAnswer.getAnswer().getId());
            detail.setAnswerText(userAnswer.getAnswer().getAnswerText());
            detail.setScore(userAnswer.getAnswer().getScore());

            // Thêm substance info cho từng câu trả lời
            if (userAnswer.getSubstance() != null) {
                AssessmentResultResponse.SubstanceDTO substanceDTO = new AssessmentResultResponse.SubstanceDTO();
                substanceDTO.setId(userAnswer.getSubstance().getId());
                substanceDTO.setName(userAnswer.getSubstance().getName());
                substanceDTO.setDescription(userAnswer.getSubstance().getDescription());
                detail.setSubstance(substanceDTO);
            }

            return detail;
        }).toList();

        response.setAnswers(answerDetails);

        // Tính điểm riêng biệt cho từng substance (ASSIST)
        if (result.getAssessment().getType() == AssessmentType.ASSIST) {
            List<AssessmentResultResponse.SubstanceResult> substanceResults = new ArrayList<>();

            // Group answers by substance
            Map<Substance, List<AssessmentResultResponse.AnswerDetail>> answersBySubstance = answerDetails.stream()
                    .filter(answer -> answer.getSubstance() != null)
                    .collect(Collectors.groupingBy(answer -> {
                        // Tạo Substance object từ DTO để group
                        Substance s = new Substance();
                        s.setId(answer.getSubstance().getId());
                        s.setName(answer.getSubstance().getName());
                        s.setDescription(answer.getSubstance().getDescription());
                        return s;
                    }));

            for (Map.Entry<Substance, List<AssessmentResultResponse.AnswerDetail>> entry : answersBySubstance.entrySet()) {
                Substance substance = entry.getKey();
                List<AssessmentResultResponse.AnswerDetail> substanceAnswers = entry.getValue();

                // Tính điểm cho substance này
                int substanceScore = substanceAnswers.stream()
                        .mapToInt(answer -> answer.getScore() != null ? answer.getScore() : 0)
                        .sum();

                // Tính risk level cho substance này
                RiskLevel substanceRiskLevel = determineSubstanceRiskLevel(AssessmentType.ASSIST, substance, substanceScore);

                // Tạo SubstanceResult
                AssessmentResultResponse.SubstanceResult substanceResult = new AssessmentResultResponse.SubstanceResult();

                AssessmentResultResponse.SubstanceDTO substanceDTO = new AssessmentResultResponse.SubstanceDTO();
                substanceDTO.setId(substance.getId());
                substanceDTO.setName(substance.getName());
                substanceDTO.setDescription(substance.getDescription());
                substanceResult.setSubstance(substanceDTO);

                substanceResult.setScore(substanceScore);
                substanceResult.setRiskLevel(substanceRiskLevel);
                substanceResult.setRiskCriteria(getSubstanceRiskCriteria(substance));
                substanceResult.setAnswers(substanceAnswers);

                substanceResults.add(substanceResult);
            }

            response.setSubstanceResults(substanceResults);
        }

        // Course recommendations logic (existing code)
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

        // Nếu là ASSIST, thu thập tất cả substances từ requests
        if (type == AssessmentType.ASSIST && !assessmentSubmissionRequests.isEmpty()) {
            Set<Long> substanceIds = assessmentSubmissionRequests.stream()
                    .map(AssessmentSubmissionRequest::getSubstanceId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<Substance> substances = new ArrayList<>();
            for (Long substanceId : substanceIds) {
                Substance substance = substanceRepository.findByIdAndIsDeletedFalse(substanceId)
                        .orElseThrow(() -> new BadRequestException("Substance not found: " + substanceId));
                substances.add(substance);
            }
            assessment.setSubstances(substances);
        }

        assessment = assessmentRepository.save(assessment);


        // 2. Xử lý từng câu trả lời với substance tracking
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

            // Thêm substance tracking cho ASSIST
            if (type == AssessmentType.ASSIST && request.getSubstanceId() != null) {
                Substance substance = substanceRepository.findByIdAndIsDeletedFalse(request.getSubstanceId())
                        .orElseThrow(() -> new BadRequestException("Substance not found: " + request.getSubstanceId()));
                userAnswer.setSubstance(substance);
            }

            userAssessmentAnswers.add(userAnswer);
        }


        // 3. Xác định mức độ rủi ro
        RiskLevel overallRiskLevel;

        if (type == AssessmentType.ASSIST && !userAssessmentAnswers.isEmpty()) {
            // Cho ASSIST: Tính risk level cao nhất từ tất cả substances
            overallRiskLevel = calculateOverallRiskLevelForAssist(userAssessmentAnswers);
        } else {
            // Cho CRAFFT: Dùng logic cũ với tổng điểm
            overallRiskLevel = determineRiskLevel(assessment.getType(), totalScore);
        }

        // 4. Tìm đề xuất rủi ro tương ứng
        RiskRecommendation recommendation = riskRecommendationRepository.findByRiskLevel(overallRiskLevel)
                .orElseThrow(() -> new BadRequestException("Missing risk config for level: " + overallRiskLevel));

        // 5. Tạo bản ghi AssessmentResult với overall risk level
        AssessmentResult result = new AssessmentResult();
        result.setAssessment(assessment);
        result.setScore(totalScore);
        result.setRiskLevel(overallRiskLevel); // Sử dụng overall risk level
        result.setRecommendation(recommendation);
        result.setDateTaken(LocalDateTime.now());
        result.setUserAnswers(userAssessmentAnswers);

        result = resultRepository.save(result);

        // 6. Gán kết quả cho từng câu trả lời của người dùng
        for (UserAssessmentAnswer answer : userAssessmentAnswers) {
            answer.setAssessmentResult(result);
        }
        userAssessmentAnswerRepository.saveAll(userAssessmentAnswers);


        // 7. Course recommendations based on overall risk level
        if (overallRiskLevel == RiskLevel.MEDIUM) {
            int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
            Course.TargetAgeGroup group = age < 18 ? Course.TargetAgeGroup.Teenagers : Course.TargetAgeGroup.Adults;

            List<Course> courses = courseRepository.findByTargetAgeGroup(group);
            for (Course course : courses) {
                RecommendationCourse recommendationCourse = new RecommendationCourse();
                RecommendationCourseId recommendationCourseId = new RecommendationCourseId(result.getId(), course.getId());
                recommendationCourse.setId(recommendationCourseId);
                recommendationCourse.setAssessmentResult(result);
                recommendationCourse.setCourse(course);
                recommendationCourseRepository.save(recommendationCourse);
            }
        }

        return convertToResponse(result);
    }

    // dùng để tính điểm riêng biệt từng bài đánh giá theo loại
    public RiskLevel determineRiskLevel(AssessmentType type, int totalScore) {
        switch (type) {
            case ASSIST:
                // Theo chuẩn WHO ASSIST:
                // 0-3: Low risk
                // 4-26: Moderate risk
                // 27+: High risk
                if (totalScore <= 3) {
                    return RiskLevel.LOW;
                } else if (totalScore <= 26) {
                    return RiskLevel.MEDIUM;
                } else {
                    return RiskLevel.HIGH;
                }

            case CRAFFT:
                if (totalScore < 1) {
                    return RiskLevel.LOW;
                } else if (totalScore < 2) {
                    return RiskLevel.MEDIUM;
                } else {
                    return RiskLevel.HIGH;
                }

            default:
                throw new IllegalArgumentException("Unsupported assessment type for risk evaluation: " + type);
        }
    }

// Tính điểm rủi ro riêng biệt cho từng substance (ASSIST)
    public RiskLevel determineSubstanceRiskLevel(AssessmentType type, Substance substance, int substanceScore) {
        if (type == AssessmentType.ASSIST) {
            // Áp dụng thang điểm khác nhau cho từng chất theo chuẩn eASSIST
            String substanceName = substance.getName().toLowerCase();

            if (substanceName.contains("alcohol")) {
                // Alcoholic beverages: 1-10 Low, 11-26 Moderate, 27+ High
                if (substanceScore <= 0) return RiskLevel.LOW;
                else if (substanceScore <= 10) return RiskLevel.LOW;
                else if (substanceScore <= 26) return RiskLevel.MEDIUM;
                else return RiskLevel.HIGH;
            } else {
                // Tất cả chất khác: 1-3 Low, 4-26 Moderate, 27+ High
                if (substanceScore <= 0) return RiskLevel.LOW;
                else if (substanceScore <= 3) return RiskLevel.LOW;
                else if (substanceScore <= 26) return RiskLevel.MEDIUM;
                else return RiskLevel.HIGH;
            }
        }

        // CRAFFT vẫn dùng logic cũ
        return determineRiskLevel(type, substanceScore);
    }

    // Lấy criteria text cho từng substance
    public String getSubstanceRiskCriteria(Substance substance) {
        String substanceName = substance.getName().toLowerCase();
        if (substanceName.contains("alcohol")) {
            return "1-10 Low, 11-26 Moderate, 27+ High";
        } else {
            return "1-3 Low, 4-26 Moderate, 27+ High";
        }
    }

    // Lấy danh sách substances cho ASSIST
    public List<Substance> getAssistSubstances() {
        return substanceRepository.findByIsDeletedFalse();
    }

    // Bắt đầu làm bài ASSIST cho substance cụ thể
    public AssessmentStartResponse startAssistForSubstance(Long substanceId) {
        // Kiểm tra substance có tồn tại không
        Substance substance = substanceRepository.findByIdAndIsDeletedFalse(substanceId)
                .orElseThrow(() -> new BadRequestException("Substance not found: " + substanceId));

        // Lấy câu hỏi cho substance này
        List<AssessmentQuestion> questions = assessmentQuestionRepository
                .findByAssessmentTypeAndSubstanceIdAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST, substanceId);

        // Nếu không có câu hỏi cho substance này, lấy câu hỏi chung
        if (questions.isEmpty()) {
            questions = assessmentQuestionRepository
                    .findByAssessmentTypeAndSubstanceIsNullAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST);
        }

        List<AssessmentStartResponse.QuestionDTO> questionDtos = questions.stream().map(question -> {
            AssessmentStartResponse.QuestionDTO qDto = new AssessmentStartResponse.QuestionDTO();
            qDto.setId(question.getId());
            // Thay thế placeholder {substance} trong câu hỏi bằng tên substance thực
            String questionText = question.getQuestionText().replace("{substance}", substance.getName().toLowerCase());
            qDto.setQuestionText(questionText);

            List<AssessmentAnswer> answers = assessmentAnswerRepository.findByQuestionIdAndIsDeletedFalse(question.getId());
            List<AssessmentStartResponse.AnswerDTO> answerDtos = answers.stream().map(a -> {
                AssessmentStartResponse.AnswerDTO aDto = new AssessmentStartResponse.AnswerDTO();
                aDto.setId(a.getId());
                aDto.setText(a.getAnswerText());
                return aDto;
            }).toList();

            qDto.setAnswers(answerDtos);
            return qDto;
        }).toList();

        AssessmentStartResponse response = new AssessmentStartResponse();
        response.setType(AssessmentType.ASSIST.name());
        response.setMessage("ASSIST assessment started for " + substance.getName());
        response.setQuestions(questionDtos);
        return response;
    }

    // Bắt đầu làm bài ASSIST với nhiều substances
    public AssessmentStartResponse startAssistForMultipleSubstances(List<Long> substanceIds) {
        // Kiểm tra tất cả substances có tồn tại không
        List<Substance> substances = new ArrayList<>();
        for (Long substanceId : substanceIds) {
            Substance substance = substanceRepository.findByIdAndIsDeletedFalse(substanceId)
                    .orElseThrow(() -> new BadRequestException("Substance not found: " + substanceId));
            substances.add(substance);
        }

        // Lấy template questions cho ASSIST (substance = null)
        List<AssessmentQuestion> templateQuestions = assessmentQuestionRepository
                .findByAssessmentTypeAndSubstanceIsNullAndIsDeletedFalseOrderByQuestionOrder(AssessmentType.ASSIST);

        List<AssessmentStartResponse.QuestionDTO> allQuestionDtos = new ArrayList<>();

        // Tạo câu hỏi cho từng substance
        for (Substance substance : substances) {
            for (AssessmentQuestion templateQuestion : templateQuestions) {
                AssessmentStartResponse.QuestionDTO qDto = new AssessmentStartResponse.QuestionDTO();
                qDto.setId(templateQuestion.getId());
                qDto.setSubstanceId(substance.getId()); // Thêm substanceId vào question

                // Thay thế placeholder {substance} bằng tên substance thực
                String questionText = templateQuestion.getQuestionText()
                        .replace("{substance}", substance.getName().toLowerCase());
                qDto.setQuestionText(questionText);

                List<AssessmentAnswer> answers = assessmentAnswerRepository
                        .findByQuestionIdAndIsDeletedFalse(templateQuestion.getId());
                List<AssessmentStartResponse.AnswerDTO> answerDtos = answers.stream().map(a -> {
                    AssessmentStartResponse.AnswerDTO aDto = new AssessmentStartResponse.AnswerDTO();
                    aDto.setId(a.getId());
                    aDto.setText(a.getAnswerText());
                    return aDto;
                }).toList();

                qDto.setAnswers(answerDtos);
                allQuestionDtos.add(qDto);
            }
        }

        AssessmentStartResponse response = new AssessmentStartResponse();
        response.setType(AssessmentType.ASSIST.name());
        response.setMessage("ASSIST assessment started for " + substances.size() + " substances");
        response.setQuestions(allQuestionDtos);
        response.setSubstances(substances.stream().map(s -> {
            AssessmentStartResponse.SubstanceDTO dto = new AssessmentStartResponse.SubstanceDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setDescription(s.getDescription());
            return dto;
        }).toList());

        return response;
    }

    // Tính overall risk level cho ASSIST dựa trên mức cao nhất của tất cả substances
    private RiskLevel calculateOverallRiskLevelForAssist(List<UserAssessmentAnswer> userAnswers) {
        // Group answers by substance
        Map<Substance, List<UserAssessmentAnswer>> answersBySubstance = userAnswers.stream()
                .filter(answer -> answer.getSubstance() != null)
                .collect(Collectors.groupingBy(UserAssessmentAnswer::getSubstance));

        RiskLevel highestRisk = RiskLevel.LOW;

        for (Map.Entry<Substance, List<UserAssessmentAnswer>> entry : answersBySubstance.entrySet()) {
            Substance substance = entry.getKey();
            List<UserAssessmentAnswer> substanceAnswers = entry.getValue();

            // Tính điểm cho substance này
            int substanceScore = substanceAnswers.stream()
                    .mapToInt(answer -> answer.getAnswer().getScore() != null ? answer.getAnswer().getScore() : 0)
                    .sum();

            // Tính risk level cho substance này
            RiskLevel substanceRisk = determineSubstanceRiskLevel(AssessmentType.ASSIST, substance, substanceScore);

            // Lấy mức rủi ro cao nhất
            if (substanceRisk == RiskLevel.HIGH) {
                highestRisk = RiskLevel.HIGH;
            } else if (substanceRisk == RiskLevel.MEDIUM && highestRisk != RiskLevel.HIGH) {
                highestRisk = RiskLevel.MEDIUM;
            }
        }

        return highestRisk;
    }
}
