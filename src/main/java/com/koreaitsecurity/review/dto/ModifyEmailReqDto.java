package com.koreaitsecurity.review.dto;

import com.koreaitsecurity.review.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyEmailReqDto {
    private String email;
    // 이것만 body로 받음
    public User toEntity(Integer userId) {
        return User.builder()
                .userId(userId) // dto 안에 같이 넣는게 아니고 pathvariable 로 따로 받아오기 때문에 this 아님
                .email(this.email)
                .build();
        // body로 입력해서 넣어줄 값
    }

}
