package com.koreaitsecurity.review.repository;

import com.koreaitsecurity.review.entity.OAuth2User;
import com.koreaitsecurity.review.mapper.OAuth2UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OAuth2UserRepository {

    @Autowired
    private OAuth2UserMapper oAuth2UserMapper;

    public OAuth2User getOAuth2UserByProviderAndProviderUserId(String provider, String providerUserId) {
        return oAuth2UserMapper.getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);
    }

    public int insertOAuth2User(OAuth2User oAuth2User) { //  Oauth2User 라는 엔티티 객체를 파라미터로 받음
        return oAuth2UserMapper.insertOAuth2User(oAuth2User); // 여기서 실제 db에 저장됨
    }

}
