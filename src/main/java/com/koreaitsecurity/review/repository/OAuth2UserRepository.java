package com.koreaitsecurity.review.repository;

import com.koreaitsecurity.review.entity.OAuth2User;
import com.koreaitsecurity.review.mapper.OAuth2UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OAuth2UserRepository {

    @Autowired
    private OAuth2UserMapper oAuth2UserMapper;

    public OAuth2User getOAuth2UserByProviderAndProviderUSerId(String provider, String providerUserId) {
        return oAuth2UserMapper.getOAuth2UserByProviderAndProviderUSerId(provider, providerUserId);
    }

}
