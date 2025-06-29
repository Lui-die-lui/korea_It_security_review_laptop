package com.koreaitsecurity.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyPasswordReqDto {
    private String oldPassword; // 원래 패스워드
    private String newPassword; // 바꾼거
    private String newPasswordCheck; // 한번더 확인
}
