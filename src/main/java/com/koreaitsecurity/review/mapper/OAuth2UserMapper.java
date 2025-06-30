package com.koreaitsecurity.review.mapper;

import com.koreaitsecurity.review.entity.OAuth2User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2UserMapper {
    OAuth2User getOAuth2UserByProviderAndProviderUserId(String provider, String providerUserId);
    int insertOAuth2User(OAuth2User oAuth2User);
}
