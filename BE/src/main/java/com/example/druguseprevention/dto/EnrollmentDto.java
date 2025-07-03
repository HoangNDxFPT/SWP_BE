package com.example.druguseprevention.dto;

import com.example.druguseprevention.entity.EnrollmentId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDto {
    //private Object id; // hoặc dùng EnrollmentId nếu FE cần
    private Long userId;
    private String userName;
    private Long courseId;
    private String courseName;
    private String status;
    private LocalDateTime enrolledAt;

    public void setId(EnrollmentId id) {
    }
}
