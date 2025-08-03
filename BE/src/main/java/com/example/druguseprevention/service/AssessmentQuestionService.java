package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.AssessmentQuestion;
import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AssessmentQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentQuestionService {

    private final AssessmentQuestionRepository questionRepository;

    public List<AssessmentQuestion> getAllQuestions() {
        return questionRepository.findAll(); // lấy tất cả, kể cả bị xóa
    }

    public List<AssessmentQuestion> getAllNotDeletedQuestions() {
        return questionRepository.findByIsDeletedFalse();
    }

    public Optional<AssessmentQuestion> getQuestionById(Long id) {
        return questionRepository.findByIdAndIsDeletedFalse(id);
    }

    public AssessmentQuestion createQuestion(AssessmentQuestion question) {
        question.setDeleted(false);
        return questionRepository.save(question);
    }

    public AssessmentQuestion updateQuestion(Long id, AssessmentQuestion updatedQuestion) {
        return questionRepository.findByIdAndIsDeletedFalse(id).map(existing -> {
            existing.setAssessmentType(updatedQuestion.getAssessmentType());
            existing.setQuestionText(updatedQuestion.getQuestionText());
            existing.setQuestionOrder(updatedQuestion.getQuestionOrder());
            return questionRepository.save(existing);
        }).orElseThrow(() -> new BadRequestException("Question not found or has been deleted"));
    }

    public void deleteQuestion(Long id) {
        questionRepository.findByIdAndIsDeletedFalse(id).ifPresent(question -> {
            question.setDeleted(true);
            questionRepository.save(question);
        });
    }
}
