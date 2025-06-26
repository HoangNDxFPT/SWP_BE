package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssessmentAnswerUpdateRequest;
import com.example.druguseprevention.entity.AssessmentAnswer;
import com.example.druguseprevention.entity.AssessmentQuestion;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AssessmentAnswerRepository;
import com.example.druguseprevention.repository.AssessmentQuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentAnswerService {

    @Autowired
    AssessmentAnswerRepository answerRepository;
    @Autowired
    AssessmentQuestionRepository assessmentQuestionRepository;

    public List<AssessmentAnswer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public List<AssessmentAnswer> getAllNotDeletedAnswers() {
        return answerRepository.findByIsDeletedFalse();
    }

    public Optional<AssessmentAnswer> getAnswerById(Long id) {
        return answerRepository.findByIdAndIsDeletedFalse(id);
    }

    public AssessmentAnswer createAnswer(AssessmentAnswer answer) {
        answer.setDeleted(false);
        return answerRepository.save(answer);
    }

    public AssessmentAnswer updateAnswer(Long id, AssessmentAnswer updatedAnswer) {
        return answerRepository.findByIdAndIsDeletedFalse(id).map(existing -> {
            existing.setAnswerText(updatedAnswer.getAnswerText());
            existing.setScore(updatedAnswer.getScore());
            existing.setQuestion(updatedAnswer.getQuestion());
            return answerRepository.save(existing);
        }).orElseThrow(() -> new BadRequestException("Answer not found or has been deleted"));
    }

    @Transactional
    public void updateAnswersByQuestionId(Long questionId, List<AssessmentAnswerUpdateRequest> updatedAnswers) {
        AssessmentQuestion question = assessmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new BadRequestException("Question not found with id: " + questionId));

        List<AssessmentAnswer> existingAnswers = answerRepository.findByQuestionIdAndIsDeletedFalse(questionId);

        // Map các câu trả lời cũ theo ID
        Map<Long, AssessmentAnswer> existingAnswerMap = existingAnswers.stream()
                .collect(Collectors.toMap(AssessmentAnswer::getId, Function.identity()));

        Set<Long> updatedIds = new HashSet<>();

        for (AssessmentAnswerUpdateRequest req : updatedAnswers) {
            if (req.getId() != null && existingAnswerMap.containsKey(req.getId())) {
                // Cập nhật câu trả lời cũ
                AssessmentAnswer answer = existingAnswerMap.get(req.getId());
                answer.setAnswerText(req.getAnswerText());
                answer.setScore(req.getScore());
                answerRepository.save(answer);
                updatedIds.add(req.getId());
            } else {
                // Thêm câu trả lời mới
                AssessmentAnswer newAnswer = new AssessmentAnswer();
                newAnswer.setQuestion(question);
                newAnswer.setAnswerText(req.getAnswerText());
                newAnswer.setScore(req.getScore());
                newAnswer.setDeleted(false);
                answerRepository.save(newAnswer);
            }
        }

        // Soft delete các câu trả lời không còn trong danh sách mới
        for (AssessmentAnswer oldAnswer : existingAnswers) {
            if (!updatedIds.contains(oldAnswer.getId())) {
                oldAnswer.setDeleted(true);
                answerRepository.save(oldAnswer);
            }
        }
    }

    public void softDeleteAnswer(Long id) {
        answerRepository.findByIdAndIsDeletedFalse(id).ifPresent(answer -> {
            answer.setDeleted(true);
            answerRepository.save(answer);
        });
    }
}