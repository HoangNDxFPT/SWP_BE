package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Lesson;

import java.util.List;
import java.util.Optional;

public interface LessonService {
    Lesson createLesson(Lesson lesson);
    Optional<Lesson> getLesson(Long id);
    List<Lesson> getLessonsByCourseId(Long courseId);
    Lesson updateLesson(Long id, Lesson lesson);
    void deleteLesson(Long id);
}
