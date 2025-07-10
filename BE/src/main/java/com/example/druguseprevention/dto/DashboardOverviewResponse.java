package com.example.druguseprevention.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardOverviewResponse {
    private long totalUsers;
    private long totalCourses;
    private long totalLessons;
    private long totalQuizzes;
    private long totalQuizSubmissions;
    private double avgQuizScore;
    private long completedCourses;
    private long totalAppointments;
    private long totalAssessments;
    private long totalSurveySent;
}
