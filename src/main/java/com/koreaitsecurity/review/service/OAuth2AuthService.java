package com.koreaitsecurity.review.service;

import com.koreaitsecurity.review.dto.ApiRespDto;
import com.koreaitsecurity.review.dto.OAuth2MergeReqDto;
import com.koreaitsecurity.review.dto.OAuth2SignupReqDto;
import com.koreaitsecurity.review.entity.User;
import com.koreaitsecurity.review.entity.UserRole;
import com.koreaitsecurity.review.repository.OAuth2UserRepository;
import com.koreaitsecurity.review.repository.UserRepository;
import com.koreaitsecurity.review.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> optionalUser = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());
        if (optionalUser.isPresent()) { // 만약 값이 비어있지 않으면
            return new ApiRespDto<>("failed","이미 존재하는 이메일 입니다.",null);
        }

        // oauth2로 새로 가입할 시,
        Optional<User> user = userRepository.addUser(oAuth2SignupReqDto.toEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(user.get().getUserId())
                .roleId(3) // 임시 사용자
                .build();
        userRoleRepository.addUserRole(userRole);
        oAuth2UserRepository
                .insertOAuth2User(oAuth2SignupReqDto.toOAuth2User(user.get().getUserId()));
        return new ApiRespDto<>("success","OAuth2 회원가입 완료",null);
    }

    // 기존 사용자와 OAuth2 사용자 병합
    public ApiRespDto<?> merge(OAuth2MergeReqDto oAuth2MergeReqDto) {
        Optional<User> optionalUser = userRepository.getUserByUsername(oAuth2MergeReqDto.getUsername());
        if (optionalUser.isEmpty()) { // 만약 유저명이 없다면
            return new ApiRespDto<>("failed", "사용자 정보를 확인하세요.",null);
        }
        if (!bCryptPasswordEncoder.matches(oAuth2MergeReqDto.getPassword(), optionalUser.get().getPassword())) {
            // 만약 비번이 일치하지 않는다면
            return new ApiRespDto<>("failed", "사용자 정보를 확인하세요.",null);
        }
        oAuth2UserRepository.insertOAuth2User(oAuth2MergeReqDto.toOAuth2User(optionalUser.get().getUserId()));
        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.",null);
    }
}
