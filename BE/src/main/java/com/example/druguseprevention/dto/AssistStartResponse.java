package com.example.druguseprevention.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssistStartResponse {
    private String message;
    private List<SubstanceOption> substances;
    private List<TemplateQuestion> templateQuestions;
    private InjectionQuestion injectionQuestion;

    @Data
    public static class SubstanceOption {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    public static class TemplateQuestion {
        private Integer questionOrder;
        private String questionTemplate; // Với placeholder [SUBSTANCE]
        private List<AnswerOption> answerOptions;
    }

    @Data
    public static class AnswerOption {
        private Long id;
        private String text;
    }

    @Data
    public static class InjectionQuestion {
        private Long questionId;
        private String questionText;
        private List<AnswerOption> answerOptions;
    }
}
