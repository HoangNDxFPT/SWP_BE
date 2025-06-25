package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.AssessmentAnswer;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AssessmentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentAnswerService {

    @Autowired
    AssessmentAnswerRepository answerRepository;

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

    public void softDeleteAnswer(Long id) {
        answerRepository.findByIdAndIsDeletedFalse(id).ifPresent(answer -> {
            answer.setDeleted(true);
            answerRepository.save(answer);
        });
    }
}