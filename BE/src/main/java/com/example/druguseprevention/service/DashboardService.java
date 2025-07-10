package com.example.druguseprevention.service;


import com.example.druguseprevention.dto.DashboardOverviewResponse;
import com.example.druguseprevention.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final CourseQuizResultRepository courseQuizResultRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final AppointmentRepository appointmentRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final SurveySendHistoryRepository surveySendHistoryRepository;

    public DashboardOverviewResponse getOverview() {
        return new DashboardOverviewResponse(
                userRepository.count(),
                courseRepository.count(),
                lessonRepository.count(),
                courseQuizRepository.count(),
                courseQuizResultRepository.count(),
                courseQuizResultRepository.getAverageScore(), // cần thêm nếu chưa có
                lessonProgressRepository.countByCompletedTrue(), // tên repo có thể khác nếu bạn dùng CourseProgress
                appointmentRepository.count(),
                assessmentResultRepository.count(),
                surveySendHistoryRepository.count()
        );
    }
}