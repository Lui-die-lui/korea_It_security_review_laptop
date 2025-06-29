package com.koreaitsecurity.review.service;

import com.koreaitsecurity.review.dto.ApiRespDto;
import com.koreaitsecurity.review.dto.ModifyEmailReqDto;
import com.koreaitsecurity.review.dto.SigninReqDto;
import com.koreaitsecurity.review.dto.SignupReqDto;
import com.koreaitsecurity.review.entity.User;
import com.koreaitsecurity.review.entity.UserRole;
import com.koreaitsecurity.review.repository.UserRepository;
import com.koreaitsecurity.review.repository.UserRoleRepository;
import com.koreaitsecurity.review.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ApiRespDto<?> addUser(SignupReqDto signupReqDto) { // 무조건 암호화 시켜야함(config의 BCryptPasswordEncoder)
        Optional<User> optionalUser = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));
        // 만들어진 유저 아이디를 가지고 userRole(유저의 역할)을 만듦. 그래서 userRole도 같이 추가해줘야함.
        // 새로 생성된 계정에 해당하는 권한도 생성해줘야함!
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3) // 3번이 임시유저 tb 이기 때문
                .build();
        userRoleRepository.addUserRole(userRole);
        return new ApiRespDto<>("success","회원가입 성공",optionalUser);
    }

    public ApiRespDto<?> signin(SigninReqDto signinReqDto) {
        Optional<User> optionalUser = userRepository.getUserByUsername(signinReqDto.getUsername());
        //Dto안에 있는 get username을 반환 - 갑시 있으면 optionalUser로 들어옴
        if (optionalUser.isEmpty()) {
            return new ApiRespDto<>("failed","사용자 정보를 확인해주세요.",null);
        }
        User user = optionalUser.get(); // 유저 객체 생성 - 여기까지 username은 일치해야 들어올 수 있음.
        // bCrypt를 써서 password가 일치하는지 봐줘야함. 아래 문장에서 bCrypt가 알아서 처리해줌
        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), user.getPassword())){
            // ! 로 반대 - 일치하지 않으면
            return new ApiRespDto<>("failed","사용자 정보를 확인해주세요.",null);
        }
        System.out.println("로그인 성공");
        // 세션 무상태로 작업하기 때문에 Filter작업으로 다 뜯어고침 - jwt token을 발행
        // 유저에게 토큰을 발행해주어야하기때문에 ApiRespDto 내에 token을 넣어줘야함

        String token = jwtUtil.generateAccessToken(user.getUserId().toString()); //user객체에서 가져온 UserId는 Integer
        // 토큰을 만들때 내가 찾아온 String 반환된 User 아이디를 넘겨주게 됨.
        return new ApiRespDto<>("Succcess","로그인 성공",token);
        // 웹 상에서는 F12 -> application -> storage쪽에 담김
        }

        public ApiRespDto<?> modifyEmail(Integer userId, ModifyEmailReqDto modifyEmailReqDto) {
            User user = modifyEmailReqDto.toEntity(userId);
            int result = userRepository.updateEmail(user);
            return new ApiRespDto<>("success","이메일 수정 성공", result);


        }
    }


