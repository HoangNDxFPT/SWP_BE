package com.example.druguseprevention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardAnalyticsResponse {
    private long newUsersThisMonth;
    private long newUsersThisWeek;
    private long activeUsersToday;
    private double courseCompletionRate;
    private Map<String, Long> riskLevelDistribution;
    private List<MonthlyStats> monthlyStats;
    private long urgentAppointments;
    private double avgAssessmentScore;

    @Data
    @AllArgsConstructor
    public static class MonthlyStats {
        private String month;
        private long newUsers;
        private long completedCourses;
        private long appointments;
    }
}
