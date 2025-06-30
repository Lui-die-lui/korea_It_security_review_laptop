package com.koreaitsecurity.review.security.handler;


import com.koreaitsecurity.review.entity.OAuth2User;
import com.koreaitsecurity.review.entity.User;
import com.koreaitsecurity.review.repository.OAuth2UserRepository;
import com.koreaitsecurity.review.repository.UserRepository;
import com.koreaitsecurity.review.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired // DB 확인용
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired // 연동된 사용자가 있다면, 가져와서 사용
    private UserRepository userRepository;

    @Autowired // 토큰 가져옴
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // Oauth2User 정보 가져오기
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        // OAuth2PrincipalUserService 내 return 값으로 지정되어있음. - getPrincipal로 가져옴
        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("id");
        String email =defaultOAuth2User.getAttribute("email");
        // provider, providerUserId 이미 연동된 사용자 정보가 있는지 DB 조회 - Repository 만들고 사용가능
        // 만든 OAuth2User 사용해야함
        OAuth2User oAuth2User = oAuth2UserRepository.getOAuth2UserByProviderAndProviderUSerId(provider, providerUserId);

        // 만약 oauth2user가 없으면,(oauth2로그인을 통해 회원가입이 되어있지 않거나 아직 연동되지 않은 상태)
        if(oAuth2User == null) {
            // 프론트로 provider와 providerUserId, email 전달
            response.sendRedirect("http//localhost:3000/auth/oauth2?provider=" + provider + "&providerUserId="+
                    providerUserId + "&email=" + email); // 리액트로 보냈다는 가정 하에
            return;
        }

        // 욘동된 사용자가 있다면? -> usesrId를 통해 회원정보 조회
        Optional<User> optionalUser = userRepository.getUserByUserId(oAuth2User.getUserId());

        // oAuth2 로그인을 통해 회원가입이나 연동을 진행한 경우(처음부터 회원가입을 연동을 통해 진행한 경우)
        String accessToken = null;
        if (optionalUser.isPresent()) { // isPresent = 비어있는지 아닌지 봐줌
            accessToken = jwtUtil.generateAccessToken(Integer.toString(optionalUser.get().getUserId()));
        }

        // 최종적으로 accessToken을 쿼리 파라미터로 프론트에 전달
        response.sendRedirect("http://localhost:3000/auth/oath2/signin?accesToken=" + accessToken);
    }
}
