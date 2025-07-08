package com.example.druguseprevention.entity;
//Dùng để lưu tiến trình học và kết quả quiz của người dùng

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lesson_progress")
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID người dùng đã học bài này
    private Long userId;

    //  Bài học liên quan
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    //  Đã học xong video/tài liệu
    private boolean completed;

//    //  Đã vượt qua bài quiz của bài học
//    private boolean quizPassed;
}

