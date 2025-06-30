package com.koreaitsecurity.review.config;

import com.koreaitsecurity.review.security.filter.JwtAuthenticationFilter;
import com.koreaitsecurity.review.security.handler.OAuth2SuccessHandler;
import com.koreaitsecurity.review.service.OAuth2PrincipalUserService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2PrincipalUserService oAuth2PrincipalUserService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean // IOC 컨테이너 객체 등록 - 다른곳에서 Autowired해서 쓸 수 있음
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration(); // 새로운 객체 생성
        // 요청을 보내는 쪽의 도메인(사이트 주소)을 허용하겠다.
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);
        // 요청을 보내는 쪽에서 Request, Response Header 정보에 대한 제약을 허용
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        // 요청을 보내는 쪽의 메소드(GET, POST, PUT, DELETE, OPTION 등) 전부 허용
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        // 요청 URL(/user/get) 에 대한 CORS 적용을 위해 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 Url(/**) 에 대해 위에서 설정한 CORS정책을 적용
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }

    /*
    * 비밀번호를 안전하게 암호화(해싱) 하고, 검증하는 역할
    * 단방향 해시, 복호화 불가능
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()); // 위에서 만든 cors설정을 security에 적용
        http.csrf(csrf -> csrf.disable()); // csrf를 사용하지 않겠다.
        // CSRF란,
        /*사용자가 의도하지 않은 요청을 공격자가 유도해서 서버에 전달하도록 하는 공격
        A가 로그인 한 상태 -> 쿠키에 있는 로그인 정보 및 세션을 이용해(낚아채) 다른 요청을 보냄
        -> 공격자가 유도한 요청대로 A의 요청이 진행되어버림.
        JWT 방식 또는 무상태(Stateless) 인증이기 때문에 세션 없음, 쿠키 안씀, 토큰기반
        CSRF 공격 자체가 성립되지 않는다.
        * */

        // 서버 사이드 렌더링 로그인 방식 비활성화
        http.formLogin(formLogin -> formLogin.disable());
        // HTTP 프로토콜 기본 로그인 방식 비활성화
        http.httpBasic(httpBasic ->httpBasic.disable());
        // 서버 사이드 렌더링 로그아웃 비활성화
        http.logout(logout -> logout.disable());
        http.sessionManagement(Session
                -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 무상태로 쓰겠다

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 특정 요청 URL에 대한 권한 설정
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/test").hasRole("ADMIN"); // 어드민 권한이어야 test 요청 가능
            // 권한을 ROLE_ADMIN, ROLE_USER처럼 저장했다면 -> hasRole("ADMIN") 가능
            // 권한을 그냥 ADMIN, USER 이렇게 저장했다면 -> hasAuthority("ADMIN") 사용
            auth.requestMatchers("/auth/signup","/auth/signin").permitAll();
            auth.anyRequest().authenticated(); // pattern에 등록된 게 아니면 전부 인증 절차를 거쳐야 한다.
        });

        // 요청이 들어오면 Spring Security의 filterChain을 탄다
        // 여기서 여러 필터 중 하나가 Oauth2 요청을 감지
        // 감지되면 해당 provider의 로그인 페이지로 리디렉션 함
        http.oauth2Login(oauth2 -> oauth2
                        // OAuth2 로그인 요청이 성공하고 사용자 정보를 가져오는 과정 설정
                .userInfoEndpoint(userInfo ->
                        // 사용자 정보 요청이 완료가 되면 이 커스텀 서비스로 OAuth2User를 처리하겠다고 설청
                        userInfo.userService(oAuth2PrincipalUserService))
                        // OAuth2 인증이 최종적으로 성공한 후 (사용자 정보 파싱이 끝난 후) 실행할 핸들러 설정
                .successHandler(oAuth2SuccessHandler));


        return http.build();

    }
}