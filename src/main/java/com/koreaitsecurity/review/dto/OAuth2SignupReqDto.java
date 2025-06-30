package com.koreaitsecurity.review.dto;

import com.koreaitsecurity.review.entity.OAuth2User;
import com.koreaitsecurity.review.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Data
public class OAuth2SignupReqDto {
    private String email;
    private String username;
    private String password;
    private String provider;
    private String providerUserId;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .username(this.username)
                .password(bCryptPasswordEncoder.encode(this.password)) // 암호화 시켜줘야하기 때문
                .email(this.email)
                .build();
    }

    public OAuth2User toOAuth2User(int userId) { // oauth2 user 객체를 만들어줌
        return OAuth2User.builder()
                .userId(userId)
                .provider(this.provider)
                .providerUserId(this.providerUserId)
                .build();
    }

}
