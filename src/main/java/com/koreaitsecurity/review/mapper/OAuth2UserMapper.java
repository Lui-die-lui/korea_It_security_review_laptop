package com.koreaitsecurity.review.mapper;

import com.koreaitsecurity.review.entity.OAuth2User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2UserMapper {
    OAuth2User getOAuth2UserByProviderAndProviderUSerId(String provider, String providerUserId);

}
