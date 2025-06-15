package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssessmentDTO;
import com.example.druguseprevention.entity.Assessment;
import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.exception.ResourceNotFoundException;
import com.example.druguseprevention.repository.AssessmentRepository;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Assessment submitAssessment(AssessmentDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Assessment assessment = new Assessment();
        assessment.setUser(user);
        assessment.setCourse(course);

        try {
            String answersJson = new ObjectMapper().writeValueAsString(dto.getAnswers());
            assessment.setAnswers(answersJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing answers", e);
        }

        assessment.setSubmittedDate(LocalDate.now());

        return assessmentRepository.save(assessment);
    }
}
