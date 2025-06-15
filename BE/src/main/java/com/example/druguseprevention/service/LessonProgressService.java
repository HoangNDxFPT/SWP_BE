package com.example.druguseprevention.service;
//Xử lý logic cập nhật tiến trình học & kết quả quiz
import com.example.druguseprevention.entity.Lesson;
import com.example.druguseprevention.entity.LessonProgress;
import com.example.druguseprevention.repository.LessonProgressRepository;
import com.example.druguseprevention.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonProgressService {

    private final LessonProgressRepository progressRepo;
    private final LessonRepository lessonRepo;

    public LessonProgressService(LessonProgressRepository progressRepo, LessonRepository lessonRepo) {
        this.progressRepo = progressRepo;
        this.lessonRepo = lessonRepo;
    }

    // Đánh dấu đã học xong
    public LessonProgress completeLesson(Long userId, Long lessonId) {
        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow();
        LessonProgress progress = progressRepo.findByUserIdAndLessonId(userId, lessonId)
                .orElse(new LessonProgress(null, userId, lesson, false, false));
        progress.setCompleted(true);
        return progressRepo.save(progress);
    }

    //  Nộp kết quả quiz
    public LessonProgress submitQuiz(Long userId, Long lessonId, boolean passed) {
        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow();
        LessonProgress progress = progressRepo.findByUserIdAndLessonId(userId, lessonId)
                .orElse(new LessonProgress(null, userId, lesson, false, false));
        progress.setQuizPassed(passed);
        return progressRepo.save(progress);
    }

    public List<LessonProgress> getUserProgress(Long userId) {
        return progressRepo.findByUserId(userId);
    }
}