package com.example.druguseprevention.service;
import com.example.druguseprevention.entity.Lesson;
import com.example.druguseprevention.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    @Override
    public Optional<Lesson> getLesson(Long id) {
        return lessonRepository.findById(id);
    }

    @Override
    public List<Lesson> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
    }

    @Override
    public Lesson updateLesson(Long id, Lesson lesson) {
        return lessonRepository.findById(id).map(existing -> {
            existing.setTitle(lesson.getTitle());
            existing.setContent(lesson.getContent());
            existing.setMaterialUrl(lesson.getMaterialUrl());
            existing.setLessonOrder(lesson.getLessonOrder());
            existing.setCourse(lesson.getCourse());
            return lessonRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    @Override
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}