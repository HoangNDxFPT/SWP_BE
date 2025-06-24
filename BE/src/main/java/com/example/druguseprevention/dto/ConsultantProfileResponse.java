package com.example.druguseprevention.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultantProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
}
