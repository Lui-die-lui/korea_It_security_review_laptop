package com.koreaitsecurity.review.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {
    private Integer userRoleId;
    private Integer userId;
    private Integer roleId;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    private Role role; // Role 객체를 가짐
}
