package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminProfileDTO extends ProfileDTO {
    private String userName;
    private String email;
    private Role role;
    private boolean deleted;
}