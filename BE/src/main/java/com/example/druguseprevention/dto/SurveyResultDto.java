package com.example.druguseprevention.dto;
import lombok.Data;
//M_SV03: Xem kết quả khảo sát & Gợi ý hành động
//
//M_SV04: Lưu lịch sử kết quả khảo sát và gợi ý
@Data
public class SurveyResultDto {
    private String question;
    private String answer;
}