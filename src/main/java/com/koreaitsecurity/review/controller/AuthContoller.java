package com.koreaitsecurity.review.controller;

import com.koreaitsecurity.review.dto.ModifyEmailReqDto;
import com.koreaitsecurity.review.dto.ModifyPasswordReqDto;
import com.koreaitsecurity.review.dto.SigninReqDto;
import com.koreaitsecurity.review.dto.SignupReqDto;
import com.koreaitsecurity.review.security.model.PrincipalUser;
import com.koreaitsecurity.review.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthContoller {

    @Autowired
    private AuthService authService;


    @GetMapping("/test")
     public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupReqDto signupReqDto) {
        return ResponseEntity.ok(authService.addUser(signupReqDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {
        return ResponseEntity.ok(authService.signin(signinReqDto));
    }
    /*"status": "Succcess",
    "message": "로그인 성공",
    "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2t
    lbiIsImp0aSI6IjEiLCJleHAiOjE3NTM2NjkyNzd9.
    79_ZqrfXs-NagNNb4jNj9v3ONS2F-_wPHGAyxvjYJaD0dsmp33JH_QRzdxi9gXAqODf8Xjoc_IB0KsZI3gYQjA" -> 로컬 스토리지에 넣어놓고 로그인 할때마다 인증 절차를 통과시켜주는 역할을 함
     -> 이게 토큰 / jwt decoder - 디코딩 해주는 사이트
     모든 정보가 나오진 않고 subject에 넣은 AccessToken, 만료기간 - 정수형 , 알고리즘 키 방식 정도 볼 수 있음.
     그래서 토큰에는 민감한 정보 넣으면 안됨.*/

    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
        // 넣어놨던 정보 가져오기 가능
        // 토큰이 없으면 애초에 여기 도달 안됨 - controller의 requestMatchers 에 따로 기입할 필요 없음
    }

    // 이메일, 패스워드 변경
    // 패스워드 = 원래 비밀번호가 일치하는지, 바꾼 비번이 일치하는지 한번 더 확인
    // 이름 중복 - 비즈니스 로직에 유저이름 중복에 대한 검사가 들어가지 않았기 때문

    @PostMapping("/{userId}")
    public ResponseEntity<?> modifyEmail(@PathVariable Integer userId, @RequestBody ModifyEmailReqDto modifyEmailReqDto) {
        return ResponseEntity.ok(authService.modifyEmail(userId, modifyEmailReqDto));
        //토큰이 필요할거고 인증이 되어있어야함 - 로그인 되어있어야 함 - Bearer token 에 토큰값 넣어줘야함!
    }

    @PostMapping("/password/{userId}")
    public ResponseEntity<?> modifyPassword(
            @PathVariable Integer userId, // 이걸로 PrincipalUser(인증 객체) 내의 userId와 비교함 - 아이디가 동일해야 변경 가능하게!
            @RequestBody ModifyPasswordReqDto modifyPasswordReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser) { // Principal 객체 들고올때 사용하는 어노테이션(인증 객체 빼옴)
        if (!userId.equals(principalUser.getUserId())) {
            return ResponseEntity.badRequest().body("본인의 계정만 변경이 가능합니다."); // 400 에러 뜨면서 메세지 반환 / .ok도 가능
        }
        return ResponseEntity.ok(authService.modifyPassword(modifyPasswordReqDto,principalUser));
        // 이미 검증되었기 때문에 principalUser에서 꺼내 써도 됨

    }
}
