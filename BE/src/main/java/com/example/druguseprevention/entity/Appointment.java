package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_time")
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // người cần tư vấn

    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private User consultant;  // người tư vấn

    public enum Status {
        PENDING, CONFIRMED, REJECTED, COMPLETED
    }
}
//| Mục tiêu                       | Mô tả                                                                           |
//| ------------------------------ | ------------------------------------------------------------------------------- |
//| **Quản lý cuộc hẹn**           | Mỗi bản ghi đại diện cho một lần người dùng đặt lịch tư vấn với một tư vấn viên |
//| **Ghi chú & xử lý trạng thái** | Tư vấn viên có thể xác nhận, từ chối, hoặc thêm ghi chú cho từng cuộc hẹn       |
//| **Gắn kết User & Consultant**  | Liên kết giữa người đặt hẹn (`user`) và người tư vấn (`consultant`)             |