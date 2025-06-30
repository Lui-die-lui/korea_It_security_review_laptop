package com.koreaitsecurity.review.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//spring security에서 기본으로 제공하는 Oauth2UserService를 상속받아 커스텀
@Service
public class OAuth2PrincipalUserService extends DefaultOAuth2UserService {

    // Oauth2 로그인 성공시 호출되는 메서드
    @Override // 메서드 재정의 - loaduser
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Spring Security가 Oauth2 provider에게 AccessToken으로 사용자 정보를 요청
        // 그 결과로 받은 사용자 정보(JSON)를 파싱한 객체를 리턴받음
        // *provider = google. kakao, naver ...etc.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사요야자 정>?<보(Map 형태) 추출
        Map<String,Object> attributes = oAuth2User.getAttributes();

        // 어떤 Oauth2 Provider인지 확인
        // proviedr => 공급처(해ㅐ힏, naver, kakao)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 로그인한 사용자의 식별자(id), 이메일
        // 로그인 시 사용한 이메일
        String email = null;
        // 공급처에서 발행한 사용자 식별자
        String id = null;

        // provider 종류에 따라 사용자 정보 파싱 방식이 다르므로 분기 처리
        switch (provider) {
            case "google" :
                id = attributes.get("sub").toString();
                email = (String) attributes.get("email");
                break;
        }

        // 우리가 필요한 정보만 골라 새롭게 attributes 구성
        Map<String, Object> newAttributes = Map.of(
                "id", id,
                "provider", provider,
                "email", email
        );

        // 권한 설정 -> 임시 권한 부여(ROLE_TEMPORARY)
        // 실제 권한은 OAuth2SuccessHandler 에서 판단
        // -> 기존에 있던 사용자(user)는 변경되면 안되기 때문(기존 회원 정보와 동일하면 연동만 타 플랫폼 연동만 시켜주는? 그런거)
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TEMPORARY"));
        // spring security가 사용할 Oauth2User 객체 생성해서 반환
        // id -> principal.getName() 으로 가져올때 사용됨
        return new DefaultOAuth2User(authorities, newAttributes,"id");




    }
}
