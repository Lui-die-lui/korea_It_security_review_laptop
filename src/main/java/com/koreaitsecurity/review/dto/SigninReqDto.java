package com.koreaitsecurity.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SigninReqDto { // 로그인 할 때/ID,PW
    private String username;
    private String password;
}
