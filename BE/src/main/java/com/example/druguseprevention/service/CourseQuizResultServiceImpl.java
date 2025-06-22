package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseQuizResultServiceImpl implements CourseQuizResultService {

    private final CourseQuizResultRepository repository;

    @Override
    public CourseQuizResult create(CourseQuizResult result) {
        return repository.save(result);
    }

    @Override
    public List<CourseQuizResult> findAll() {
        return repository.findAll();
    }

    @Override
    public CourseQuizResult findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    @Override
    public CourseQuizResult update(Long id, CourseQuizResult updatedResult) {
        CourseQuizResult existing = findById(id);
        existing.setScore(updatedResult.getScore());
        existing.setTotalQuestions(updatedResult.getTotalQuestions());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}