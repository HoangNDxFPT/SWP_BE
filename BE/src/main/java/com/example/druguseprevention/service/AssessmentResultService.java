package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssessmentResultResponse;
import com.example.druguseprevention.entity.*;
import com.example.druguseprevention.enums.RiskLevel;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentResultService {

    private final AssessmentResultRepository resultRepository;

    private final AssessmentService assessmentService;

    private final UserAssessmentAnswerRepository userAssessmentAnswerRepository;

    private final RecommendationCourseRepository recommendationCourseRepository;

    public AssessmentResultResponse getResultById(Long id) {
        AssessmentResult result = resultRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + id));
        return assessmentService.convertToResponse(result);
    }

    public List<AssessmentResultResponse> getResultsByUserId(Long userId) {
        List<AssessmentResult> results = resultRepository.findByAssessmentMemberId(userId);
        return results.stream()
                .map(assessmentService::convertToResponse) // Dùng lại hàm từ AssessmentService
                .toList();
    }

    public Map<String, Object> getResultBySubstance(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        Map<String, Object> response = new HashMap<>();
        response.put("assessmentResultId", result.getId());
        response.put("assessmentType", result.getAssessment().getType());
        response.put("overallRiskLevel", result.getRiskLevel());
        response.put("totalScore", result.getScore());
        response.put("submittedAt", result.getDateTaken());

        // Lấy tất cả substances được sử dụng
        List<Substance> substances = userAssessmentAnswerRepository.findDistinctSubstancesByAssessmentResultId(resultId);

        List<Map<String, Object>> substanceDetails = substances.stream().map(substance -> {
            Map<String, Object> substanceData = new HashMap<>();
            substanceData.put("substanceId", substance.getId());
            substanceData.put("substanceName", substance.getName());
            substanceData.put("substanceDescription", substance.getDescription());

            // Lấy câu trả lời cho substance này
            List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository
                    .findByAssessmentResultIdAndSubstanceId(resultId, substance.getId());

            List<Map<String, Object>> answerDetails = answers.stream().map(answer -> {
                Map<String, Object> answerData = new HashMap<>();
                answerData.put("questionText", answer.getQuestion().getQuestionText().replace("[SUBSTANCE]", substance.getName()));
                answerData.put("answerText", answer.getAnswer().getAnswerText());
                answerData.put("score", answer.getAnswer().getScore());
                answerData.put("questionOrder", answer.getQuestion().getQuestionOrder());
                return answerData;
            }).collect(Collectors.toList());

            // Tính điểm cho substance này
            int substanceScore = answers.stream()
                    .mapToInt(answer -> Optional.ofNullable(answer.getAnswer().getScore()).orElse(0))
                    .sum();

            substanceData.put("score", substanceScore);
            substanceData.put("answers", answerDetails);

            // Tính risk level cho substance này
            RiskLevel substanceRiskLevel = determineSubstanceRiskLevel(substance.getName(), substanceScore);
            substanceData.put("riskLevel", substanceRiskLevel);
            substanceData.put("criteria", getRiskCriteria(substance.getName()));

            return substanceData;
        }).collect(Collectors.toList());

        response.put("substanceResults", substanceDetails);

        // Lấy câu trả lời injection (không có substance)
        List<UserAssessmentAnswer> injectionAnswers = userAssessmentAnswerRepository
                .findByAssessmentResultIdAndSubstanceIsNull(resultId);

        if (!injectionAnswers.isEmpty()) {
            List<Map<String, Object>> injectionDetails = injectionAnswers.stream().map(answer -> {
                Map<String, Object> injectionData = new HashMap<>();
                injectionData.put("questionText", answer.getQuestion().getQuestionText());
                injectionData.put("answerText", answer.getAnswer().getAnswerText());
                injectionData.put("score", answer.getAnswer().getScore());
                return injectionData;
            }).collect(Collectors.toList());

            response.put("injectionAnswers", injectionDetails);
        }

        return response;
    }

    public List<Map<String, Object>> getSubstancesInResult(Long resultId) {
        List<Substance> substances = userAssessmentAnswerRepository.findDistinctSubstancesByAssessmentResultId(resultId);

        return substances.stream().map(substance -> {
            Map<String, Object> substanceInfo = new HashMap<>();
            substanceInfo.put("substanceId", substance.getId());
            substanceInfo.put("substanceName", substance.getName());
            substanceInfo.put("substanceDescription", substance.getDescription());

            // Đếm số câu trả lời cho substance này
            List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository
                    .findByAssessmentResultIdAndSubstanceId(resultId, substance.getId());
            substanceInfo.put("answerCount", answers.size());

            return substanceInfo;
        }).collect(Collectors.toList());
    }

    private RiskLevel determineSubstanceRiskLevel(String substanceName, int score) {
        if (substanceName.toLowerCase().contains("alcohol")) {
            if (score <= 10) return RiskLevel.LOW;
            else if (score <= 26) return RiskLevel.MEDIUM;
            else return RiskLevel.HIGH;
        } else {
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

    public Map<String, Object> getScoringDetails(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        Map<String, Object> response = new HashMap<>();
        response.put("assessmentResultId", result.getId());
        response.put("assessmentType", result.getAssessment().getType());
        response.put("totalScore", result.getScore());
        response.put("overallRiskLevel", result.getRiskLevel());
        response.put("submittedAt", result.getDateTaken());

        // Lấy tất cả substances và tính điểm chi tiết
        List<Substance> substances = userAssessmentAnswerRepository.findDistinctSubstancesByAssessmentResultId(resultId);

        List<Map<String, Object>> substanceScoring = new ArrayList<>();
        int totalCalculatedScore = 0;

        for (Substance substance : substances) {
            Map<String, Object> substanceData = new HashMap<>();
            substanceData.put("substanceId", substance.getId());
            substanceData.put("substanceName", substance.getName());

            // Lấy câu trả lời theo thứ tự câu hỏi
            List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository
                    .findByAssessmentResultIdAndSubstanceId(resultId, substance.getId());

            // Tạo breakdown điểm theo từng câu hỏi
            Map<String, Object> scoreBreakdown = new HashMap<>();
            int substanceScore = 0;

            for (UserAssessmentAnswer answer : answers) {
                int questionOrder = answer.getQuestion().getQuestionOrder();
                int answerScore = Optional.ofNullable(answer.getAnswer().getScore()).orElse(0);
                substanceScore += answerScore;

                String questionKey = "Q" + questionOrder;
                Map<String, Object> questionDetail = new HashMap<>();
                questionDetail.put("questionText", answer.getQuestion().getQuestionText().replace("[SUBSTANCE]", substance.getName()));
                questionDetail.put("answerText", answer.getAnswer().getAnswerText());
                questionDetail.put("score", answerScore);
                questionDetail.put("maxPossibleScore", getMaxScoreForQuestion(questionOrder));

                scoreBreakdown.put(questionKey, questionDetail);
            }

            substanceData.put("scoreBreakdown", scoreBreakdown);
            substanceData.put("substanceTotal", substanceScore);
            substanceData.put("riskLevel", determineSubstanceRiskLevel(substance.getName(), substanceScore));
            substanceData.put("criteria", getRiskCriteria(substance.getName()));
            substanceData.put("riskThresholds", getRiskThresholds(substance.getName()));

            totalCalculatedScore += substanceScore;
            substanceScoring.add(substanceData);
        }

        response.put("substanceScoring", substanceScoring);
        response.put("calculatedTotalScore", totalCalculatedScore);
        response.put("scoringExplanation", getScoringExplanation());

        return response;
    }

    public Map<String, Object> getRiskAnalysis(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        Map<String, Object> response = new HashMap<>();
        response.put("assessmentResultId", result.getId());
        response.put("overallRiskLevel", result.getRiskLevel());
        response.put("totalScore", result.getScore());

        // Phân tích rủi ro tổng quát
        Map<String, Object> riskAnalysis = new HashMap<>();
        riskAnalysis.put("level", result.getRiskLevel());
        riskAnalysis.put("description", getRiskDescription(result.getRiskLevel()));
        riskAnalysis.put("recommendations", getRiskRecommendations(result.getRiskLevel()));
        riskAnalysis.put("interventions", getInterventions(result.getRiskLevel()));

        response.put("riskAnalysis", riskAnalysis);

        // Phân tích theo từng substance
        List<Substance> substances = userAssessmentAnswerRepository.findDistinctSubstancesByAssessmentResultId(resultId);
        List<Map<String, Object>> substanceRisks = new ArrayList<>();

        for (Substance substance : substances) {
            List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository
                    .findByAssessmentResultIdAndSubstanceId(resultId, substance.getId());

            int substanceScore = answers.stream()
                    .mapToInt(answer -> Optional.ofNullable(answer.getAnswer().getScore()).orElse(0))
                    .sum();

            RiskLevel substanceRisk = determineSubstanceRiskLevel(substance.getName(), substanceScore);

            Map<String, Object> substanceRiskData = new HashMap<>();
            substanceRiskData.put("substanceId", substance.getId());
            substanceRiskData.put("substanceName", substance.getName());
            substanceRiskData.put("score", substanceScore);
            substanceRiskData.put("riskLevel", substanceRisk);
            substanceRiskData.put("riskFactors", analyzeRiskFactors(answers));
            substanceRiskData.put("specificRecommendations", getSubstanceSpecificRecommendations(substance.getName(), substanceRisk));

            substanceRisks.add(substanceRiskData);
        }

        response.put("substanceRisks", substanceRisks);
        response.put("overallAssessment", generateOverallAssessment(result.getRiskLevel(), result.getScore()));

        return response;
    }

    // Helper methods for scoring system
    private int getMaxScoreForQuestion(int questionOrder) {
        if (questionOrder >= 2 && questionOrder <= 7) {
            return 6; // Maximum score for frequency-based questions
        } else if (questionOrder == 8) {
            return 2; // Maximum score for injection question
        }
        return 0;
    }

    private Map<String, Integer> getRiskThresholds(String substanceName) {
        Map<String, Integer> thresholds = new HashMap<>();
        if (substanceName.toLowerCase().contains("alcohol")) {
            thresholds.put("low", 10);
            thresholds.put("moderate", 26);
            thresholds.put("high", 27);
        } else {
            thresholds.put("low", 3);
            thresholds.put("moderate", 26);
            thresholds.put("high", 27);
        }
        return thresholds;
    }

    private String getScoringExplanation() {
        return "ASSIST scoring system: Each question has different point values. " +
                "Q2-Q5 (frequency questions): Never=0, Once/twice=2, Monthly=3, Weekly=4, Daily=6. " +
                "Q6-Q7 (concern/control): No=0, Yes recent=6, Yes past=3. " +
                "Q8 (injection): No=0, Yes recent=2, Yes past=1.";
    }

    private String getRiskDescription(RiskLevel riskLevel) {
        switch (riskLevel) {
            case LOW:
                return "Low risk level indicates minimal substance use issues. No immediate intervention required.";
            case MEDIUM:
                return "Moderate risk level suggests problematic substance use patterns. Brief intervention recommended.";
            case HIGH:
                return "High risk level indicates severe substance use problems. Intensive treatment required.";
            default:
                return "Risk level not determined.";
        }
    }

    private List<String> getRiskRecommendations(RiskLevel riskLevel) {
        List<String> recommendations = new ArrayList<>();
        switch (riskLevel) {
            case LOW:
                recommendations.add("Continue current healthy behaviors");
                recommendations.add("Regular health monitoring");
                recommendations.add("Prevention education");
                break;
            case MEDIUM:
                recommendations.add("Brief intervention counseling");
                recommendations.add("Self-monitoring of substance use");
                recommendations.add("Follow-up assessment in 3-6 months");
                recommendations.add("Consider support groups");
                break;
            case HIGH:
                recommendations.add("Immediate referral to addiction specialist");
                recommendations.add("Comprehensive assessment");
                recommendations.add("Consider inpatient or intensive outpatient treatment");
                recommendations.add("Medical evaluation for withdrawal management");
                recommendations.add("Family/social support involvement");
                break;
        }
        return recommendations;
    }

    private List<String> getInterventions(RiskLevel riskLevel) {
        List<String> interventions = new ArrayList<>();
        switch (riskLevel) {
            case LOW:
                interventions.add("Health education");
                interventions.add("Risk awareness");
                break;
            case MEDIUM:
                interventions.add("Motivational interviewing");
                interventions.add("Behavioral counseling");
                interventions.add("Skill building");
                break;
            case HIGH:
                interventions.add("Detoxification if needed");
                interventions.add("Cognitive behavioral therapy");
                interventions.add("Medication-assisted treatment");
                interventions.add("Relapse prevention");
                break;
        }
        return interventions;
    }

    private List<String> analyzeRiskFactors(List<UserAssessmentAnswer> answers) {
        List<String> riskFactors = new ArrayList<>();

        for (UserAssessmentAnswer answer : answers) {
            int questionOrder = answer.getQuestion().getQuestionOrder();
            int score = Optional.ofNullable(answer.getAnswer().getScore()).orElse(0);

            if (score > 0) {
                switch (questionOrder) {
                    case 2:
                        if (score >= 4) riskFactors.add("High frequency of use");
                        break;
                    case 3:
                        if (score >= 4) riskFactors.add("Strong cravings/urges");
                        break;
                    case 4:
                        if (score >= 3) riskFactors.add("Substance-related problems");
                        break;
                    case 5:
                        if (score >= 3) riskFactors.add("Functional impairment");
                        break;
                    case 6:
                        if (score >= 3) riskFactors.add("Social concern");
                        break;
                    case 7:
                        if (score >= 3) riskFactors.add("Loss of control");
                        break;
                    case 8:
                        if (score > 0) riskFactors.add("Injection drug use");
                        break;
                }
            }
        }

        return riskFactors;
    }

    private List<String> getSubstanceSpecificRecommendations(String substanceName, RiskLevel riskLevel) {
        List<String> recommendations = new ArrayList<>();

        if (substanceName.toLowerCase().contains("alcohol")) {
            switch (riskLevel) {
                case MEDIUM:
                    recommendations.add("Consider alcohol moderation strategies");
                    recommendations.add("Monitor alcohol consumption");
                    break;
                case HIGH:
                    recommendations.add("Alcohol detoxification may be needed");
                    recommendations.add("Consider alcohol use disorder treatment");
                    break;
            }
        } else if (substanceName.toLowerCase().contains("tobacco")) {
            switch (riskLevel) {
                case MEDIUM:
                case HIGH:
                    recommendations.add("Tobacco cessation program");
                    recommendations.add("Nicotine replacement therapy");
                    break;
            }
        } else {
            switch (riskLevel) {
                case MEDIUM:
                    recommendations.add("Substance-specific counseling");
                    recommendations.add("Harm reduction strategies");
                    break;
                case HIGH:
                    recommendations.add("Specialized addiction treatment");
                    recommendations.add("Medical supervision may be required");
                    break;
            }
        }

        return recommendations;
    }

    private String generateOverallAssessment(RiskLevel riskLevel, int totalScore) {
        return String.format(
                "Overall Assessment: %s risk level with total score of %d. " +
                        "This indicates %s substance use patterns requiring %s level of intervention.",
                riskLevel,
                totalScore,
                riskLevel == RiskLevel.LOW ? "minimal" : riskLevel == RiskLevel.MEDIUM ? "moderate" : "severe",
                riskLevel == RiskLevel.LOW ? "preventive" : riskLevel == RiskLevel.MEDIUM ? "brief" : "intensive"
        );
    }

    // =============== CRAFFT-specific methods ===============

    public Map<String, Object> getCrafftResult(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        // Validate this is a CRAFFT assessment
        if (result.getAssessment().getType() != com.example.druguseprevention.enums.AssessmentType.CRAFFT) {
            throw new BadRequestException("This is not a CRAFFT assessment result");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("assessmentResultId", result.getId());
        response.put("assessmentType", "CRAFFT");
        response.put("score", result.getScore());
        response.put("riskLevel", result.getRiskLevel());
        response.put("submittedAt", result.getDateTaken());
        response.put("recommendation", result.getRecommendation().getMessage());

        // Get all answers for CRAFFT (no substances)
        List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository.findByAssessmentResultId(resultId);

        List<Map<String, Object>> answerDetails = answers.stream().map(answer -> {
            Map<String, Object> answerData = new HashMap<>();
            answerData.put("questionId", answer.getQuestion().getId());
            answerData.put("questionText", answer.getQuestion().getQuestionText());
            answerData.put("questionOrder", answer.getQuestion().getQuestionOrder());
            answerData.put("answerId", answer.getAnswer().getId());
            answerData.put("answerText", answer.getAnswer().getAnswerText());
            answerData.put("score", answer.getAnswer().getScore());
            answerData.put("selectedAt", answer.getSelectedAt());
            return answerData;
        }).collect(Collectors.toList());

        response.put("answers", answerDetails);
        response.put("interpretation", getCrafftInterpretation(result.getScore(), result.getRiskLevel()));

        return response;
    }

    public Map<String, Object> getCrafftScoring(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        if (result.getAssessment().getType() != com.example.druguseprevention.enums.AssessmentType.CRAFFT) {
            throw new BadRequestException("This is not a CRAFFT assessment result");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("assessmentResultId", result.getId());
        response.put("totalScore", result.getScore());
        response.put("maxPossibleScore", 6); // CRAFFT has 6 questions, each worth 1 point
        response.put("riskLevel", result.getRiskLevel());

        // Get scoring breakdown
        List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository.findByAssessmentResultId(resultId);
        Map<String, Object> scoreBreakdown = new HashMap<>();

        for (UserAssessmentAnswer answer : answers) {
            String questionKey = "Q" + answer.getQuestion().getQuestionOrder();
            Map<String, Object> questionScore = new HashMap<>();
            questionScore.put("questionText", answer.getQuestion().getQuestionText());
            questionScore.put("answerText", answer.getAnswer().getAnswerText());
            questionScore.put("score", answer.getAnswer().getScore());
            questionScore.put("maxScore", 1);
            scoreBreakdown.put(questionKey, questionScore);
        }

        response.put("scoreBreakdown", scoreBreakdown);
        response.put("scoringCriteria", getCrafftScoringCriteria());
        response.put("riskThresholds", getCrafftRiskThresholds());

        return response;
    }

    public Map<String, Object> getCrafftRiskAnalysis(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        if (result.getAssessment().getType() != com.example.druguseprevention.enums.AssessmentType.CRAFFT) {
            throw new BadRequestException("This is not a CRAFFT assessment result");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("assessmentResultId", result.getId());
        response.put("score", result.getScore());
        response.put("riskLevel", result.getRiskLevel());

        // CRAFFT-specific risk analysis
        Map<String, Object> riskAnalysis = new HashMap<>();
        riskAnalysis.put("level", result.getRiskLevel());
        riskAnalysis.put("description", getCrafftRiskDescription(result.getRiskLevel(), result.getScore()));
        riskAnalysis.put("recommendations", getCrafftRecommendations(result.getRiskLevel()));
        riskAnalysis.put("nextSteps", getCrafftNextSteps(result.getRiskLevel()));

        response.put("riskAnalysis", riskAnalysis);

        // Analyze risk factors from answers
        List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository.findByAssessmentResultId(resultId);
        List<String> riskFactors = analyzeCrafftRiskFactors(answers);
        response.put("identifiedRiskFactors", riskFactors);

        response.put("overallAssessment", generateCrafftAssessment(result.getRiskLevel(), result.getScore()));

        return response;
    }

    public List<Map<String, Object>> getMyCrafftHistory(Long userId) {
        List<AssessmentResult> results = resultRepository.findByAssessmentMemberId(userId);

        return results.stream()
                .filter(result -> result.getAssessment().getType() == com.example.druguseprevention.enums.AssessmentType.CRAFFT)
                .map(result -> {
                    Map<String, Object> crafftResult = new HashMap<>();
                    crafftResult.put("assessmentResultId", result.getId());
                    crafftResult.put("score", result.getScore());
                    crafftResult.put("riskLevel", result.getRiskLevel());
                    crafftResult.put("submittedAt", result.getDateTaken());
                    crafftResult.put("recommendation", result.getRecommendation().getMessage());
                    return crafftResult;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getLatestCrafftResult(Long userId) {
        List<AssessmentResult> results = resultRepository.findByAssessmentMemberId(userId);

        Optional<AssessmentResult> latestCrafft = results.stream()
                .filter(result -> result.getAssessment().getType() == com.example.druguseprevention.enums.AssessmentType.CRAFFT)
                .max(Comparator.comparing(AssessmentResult::getDateTaken));

        if (latestCrafft.isEmpty()) {
            throw new BadRequestException("No CRAFFT assessment found for this user");
        }

        return getCrafftResult(latestCrafft.get().getId());
    }

    // Helper methods for CRAFFT
    private String getCrafftInterpretation(int score, RiskLevel riskLevel) {
        switch (riskLevel) {
            case LOW:
                return "Score 0-1: Low risk. No immediate substance abuse intervention needed. Continue monitoring and provide prevention education.";
            case MEDIUM:
                return "Score 2-3: Moderate risk. Brief intervention and closer monitoring recommended. Consider counseling services.";
            case HIGH:
                return "Score 4-6: High risk. Comprehensive assessment and specialized treatment strongly recommended.";
            default:
                return "Unable to determine risk interpretation.";
        }
    }

    private Map<String, Object> getCrafftScoringCriteria() {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("description", "CRAFFT scoring system: Each question answered 'Yes' scores 1 point, 'No' scores 0 points");
        criteria.put("totalQuestions", 6);
        criteria.put("maxScore", 6);
        criteria.put("scoringMethod", "Binary scoring (0 or 1 per question)");
        return criteria;
    }

    private Map<String, Integer> getCrafftRiskThresholds() {
        Map<String, Integer> thresholds = new HashMap<>();
        thresholds.put("low", 1);
        thresholds.put("moderate", 3);
        thresholds.put("high", 4);
        return thresholds;
    }

    private String getCrafftRiskDescription(RiskLevel riskLevel, int score) {
        switch (riskLevel) {
            case LOW:
                return String.format("Low risk (Score: %d/6). Minimal substance use concerns. Preventive education recommended.", score);
            case MEDIUM:
                return String.format("Moderate risk (Score: %d/6). Some concerning patterns identified. Brief intervention recommended.", score);
            case HIGH:
                return String.format("High risk (Score: %d/6). Significant substance use concerns. Comprehensive evaluation needed.", score);
            default:
                return String.format("Risk level not determined (Score: %d/6).", score);
        }
    }

    private List<String> getCrafftRecommendations(RiskLevel riskLevel) {
        List<String> recommendations = new ArrayList<>();
        switch (riskLevel) {
            case LOW:
                recommendations.add("Continue current healthy behaviors");
                recommendations.add("Provide substance abuse prevention education");
                recommendations.add("Regular check-ins during routine visits");
                break;
            case MEDIUM:
                recommendations.add("Brief intervention counseling");
                recommendations.add("Increased monitoring and follow-up");
                recommendations.add("Involve parents/guardians in discussion");
                recommendations.add("Consider referral to counseling services");
                break;
            case HIGH:
                recommendations.add("Comprehensive substance abuse assessment");
                recommendations.add("Referral to specialized treatment services");
                recommendations.add("Family involvement in treatment planning");
                recommendations.add("Consider intensive outpatient or inpatient treatment");
                recommendations.add("Coordinate with mental health professionals");
                break;
        }
        return recommendations;
    }

    private List<String> getCrafftNextSteps(RiskLevel riskLevel) {
        List<String> nextSteps = new ArrayList<>();
        switch (riskLevel) {
            case LOW:
                nextSteps.add("Schedule routine follow-up in 6-12 months");
                nextSteps.add("Provide educational materials");
                nextSteps.add("Discuss healthy coping strategies");
                break;
            case MEDIUM:
                nextSteps.add("Schedule follow-up within 1-3 months");
                nextSteps.add("Initiate brief intervention");
                nextSteps.add("Assess family and social support");
                nextSteps.add("Monitor for escalation of use");
                break;
            case HIGH:
                nextSteps.add("Immediate referral to substance abuse specialist");
                nextSteps.add("Safety assessment");
                nextSteps.add("Coordinate care with treatment team");
                nextSteps.add("Plan for ongoing monitoring and support");
                break;
        }
        return nextSteps;
    }

    private List<String> analyzeCrafftRiskFactors(List<UserAssessmentAnswer> answers) {
        List<String> riskFactors = new ArrayList<>();

        for (UserAssessmentAnswer answer : answers) {
            if (answer.getAnswer().getScore() == 1) { // "Yes" answers indicate risk
                String questionText = answer.getQuestion().getQuestionText().toLowerCase();

                if (questionText.contains("car")) {
                    riskFactors.add("Rides in car with someone under the influence");
                } else if (questionText.contains("relax")) {
                    riskFactors.add("Uses substances to relax or feel better");
                } else if (questionText.contains("alone")) {
                    riskFactors.add("Uses substances when alone");
                } else if (questionText.contains("forget")) {
                    riskFactors.add("Forgets things while using substances");
                } else if (questionText.contains("family") || questionText.contains("friends")) {
                    riskFactors.add("Family or friends express concern about substance use");
                } else if (questionText.contains("trouble")) {
                    riskFactors.add("Has gotten into trouble while using substances");
                }
            }
        }

        return riskFactors;
    }

    private String generateCrafftAssessment(RiskLevel riskLevel, int score) {
        return String.format(
                "CRAFFT Assessment: %s risk level with score of %d/6. " +
                        "This indicates %s level of substance use concern requiring %s intervention for adolescents/young adults.",
                riskLevel,
                score,
                riskLevel == RiskLevel.LOW ? "minimal" : riskLevel == RiskLevel.MEDIUM ? "moderate" : "high",
                riskLevel == RiskLevel.LOW ? "preventive" : riskLevel == RiskLevel.MEDIUM ? "brief" : "intensive"
        );
    }

    // =============== Simplified Main Methods ===============

    public Map<String, Object> getCompleteResult(Long resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + resultId));

        Map<String, Object> response = new HashMap<>();

        // Basic info
        response.put("assessmentResultId", result.getId());
        response.put("assessmentType", result.getAssessment().getType());
        response.put("score", result.getScore());
        response.put("riskLevel", result.getRiskLevel());
        response.put("submittedAt", result.getDateTaken());
        response.put("recommendation", result.getRecommendation().getMessage());

        // Recommended courses if MEDIUM risk
        if (result.getRiskLevel() == RiskLevel.MEDIUM) {
            List<RecommendationCourse> recCourses = recommendationCourseRepository
                    .findByIdAssessmentResultId(result.getId());
            List<Map<String, Object>> courseDTOs = recCourses.stream().map(rc -> {
                Course course = rc.getCourse();
                Map<String, Object> courseDTO = new HashMap<>();
                courseDTO.put("id", course.getId());
                courseDTO.put("name", course.getName());
                courseDTO.put("description", course.getDescription());
                courseDTO.put("targetAgeGroup", course.getTargetAgeGroup().name());
                return courseDTO;
            }).collect(Collectors.toList());
            response.put("recommendedCourses", courseDTOs);
        } else {
            response.put("recommendedCourses", Collections.emptyList());
        }

        // Assessment specific details
        if (result.getAssessment().getType() == com.example.druguseprevention.enums.AssessmentType.ASSIST) {
            // ASSIST - with substances
            addAssistDetails(response, resultId);
        } else if (result.getAssessment().getType() == com.example.druguseprevention.enums.AssessmentType.CRAFFT) {
            // CRAFFT - without substances
            addCrafftDetails(response, result);
        }

        return response;
    }

    public List<Map<String, Object>> getMyResults(Long userId) {
        List<AssessmentResult> results = resultRepository.findByAssessmentMemberId(userId);

        return results.stream().map(result -> {
            Map<String, Object> summary = new HashMap<>();
            summary.put("assessmentResultId", result.getId());
            summary.put("assessmentType", result.getAssessment().getType());
            summary.put("score", result.getScore());
            summary.put("riskLevel", result.getRiskLevel());
            summary.put("submittedAt", result.getDateTaken());
            summary.put("recommendation", result.getRecommendation().getMessage());

            // Add course count if MEDIUM
            if (result.getRiskLevel() == RiskLevel.MEDIUM) {
                int courseCount = recommendationCourseRepository
                        .findByIdAssessmentResultId(result.getId()).size();
                summary.put("recommendedCoursesCount", courseCount);
            }

            return summary;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getMyLatestResult(Long userId) {
        List<AssessmentResult> results = resultRepository.findByAssessmentMemberId(userId);

        Optional<AssessmentResult> latestResult = results.stream()
                .max(Comparator.comparing(AssessmentResult::getDateTaken));

        if (latestResult.isEmpty()) {
            throw new BadRequestException("No assessment result found for this user");
        }

        return getCompleteResult(latestResult.get().getId());
    }

    // Helper methods
    private void addAssistDetails(Map<String, Object> response, Long resultId) {
        // Get substances
        List<Substance> substances = userAssessmentAnswerRepository.findDistinctSubstancesByAssessmentResultId(resultId);

        List<Map<String, Object>> substanceResults = substances.stream().map(substance -> {
            Map<String, Object> substanceData = new HashMap<>();
            substanceData.put("substanceId", substance.getId());
            substanceData.put("substanceName", substance.getName());
            substanceData.put("substanceDescription", substance.getDescription());

            // Get answers for this substance
            List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository
                    .findByAssessmentResultIdAndSubstanceId(resultId, substance.getId());

            int substanceScore = answers.stream()
                    .mapToInt(answer -> Optional.ofNullable(answer.getAnswer().getScore()).orElse(0))
                    .sum();

            substanceData.put("substanceScore", substanceScore); // Đổi từ "score" thành "substanceScore"
            substanceData.put("riskLevel", determineSubstanceRiskLevel(substance.getName(), substanceScore));
            substanceData.put("criteria", getRiskCriteria(substance.getName()));

            // Add answers
            List<Map<String, Object>> answerDetails = answers.stream().map(answer -> {
                Map<String, Object> answerData = new HashMap<>();
                String questionText = answer.getQuestion().getQuestionText();
                if (answer.getQuestion().getQuestionOrder() != 8) {
                    questionText = questionText.replace("[SUBSTANCE]", substance.getName());
                }
                answerData.put("questionText", questionText);
                answerData.put("answerText", answer.getAnswer().getAnswerText());
                answerData.put("score", answer.getAnswer().getScore());
                answerData.put("questionOrder", answer.getQuestion().getQuestionOrder());
                return answerData;
            }).collect(Collectors.toList());

            substanceData.put("answers", answerDetails);
            return substanceData;
        }).collect(Collectors.toList());

        response.put("substanceResults", substanceResults);

        // Thêm thông tin tổng quan ASSIST
        Map<String, Object> assistSummary = new HashMap<>();
        assistSummary.put("totalSubstances", substances.size());
        assistSummary.put("highestSubstanceScore", substanceResults.stream()
                .mapToInt(sub -> (Integer) sub.get("substanceScore"))
                .max().orElse(0));
        assistSummary.put("averageSubstanceScore", substanceResults.stream()
                .mapToInt(sub -> (Integer) sub.get("substanceScore"))
                .average().orElse(0.0));

        response.put("assistSummary", assistSummary);

        // Add injection answers if any
        List<UserAssessmentAnswer> injectionAnswers = userAssessmentAnswerRepository
                .findByAssessmentResultIdAndSubstanceIsNull(resultId);

        if (!injectionAnswers.isEmpty()) {
            List<Map<String, Object>> injectionDetails = injectionAnswers.stream().map(answer -> {
                Map<String, Object> injectionData = new HashMap<>();
                injectionData.put("questionText", answer.getQuestion().getQuestionText());
                injectionData.put("answerText", answer.getAnswer().getAnswerText());
                injectionData.put("score", answer.getAnswer().getScore());
                return injectionData;
            }).collect(Collectors.toList());
            response.put("injectionAnswers", injectionDetails);
        }
    }

    private void addCrafftDetails(Map<String, Object> response, AssessmentResult result) {
        // Get all answers for CRAFFT
        List<UserAssessmentAnswer> answers = userAssessmentAnswerRepository.findByAssessmentResultId(result.getId());

        List<Map<String, Object>> answerDetails = answers.stream().map(answer -> {
            Map<String, Object> answerData = new HashMap<>();
            answerData.put("questionId", answer.getQuestion().getId());
            answerData.put("questionText", answer.getQuestion().getQuestionText());
            answerData.put("questionOrder", answer.getQuestion().getQuestionOrder());
            answerData.put("answerId", answer.getAnswer().getId());
            answerData.put("answerText", answer.getAnswer().getAnswerText());
            answerData.put("score", answer.getAnswer().getScore());
            answerData.put("selectedAt", answer.getSelectedAt());
            return answerData;
        }).collect(Collectors.toList());

        response.put("answers", answerDetails);
        response.put("interpretation", getCrafftInterpretation(result.getScore(), result.getRiskLevel()));
        response.put("maxPossibleScore", 6);

        // Risk factors
        List<String> riskFactors = analyzeCrafftRiskFactors(answers);
        response.put("identifiedRiskFactors", riskFactors);

        // Recommendations
        response.put("recommendations", getCrafftRecommendations(result.getRiskLevel()));
        response.put("nextSteps", getCrafftNextSteps(result.getRiskLevel()));
    }
}
