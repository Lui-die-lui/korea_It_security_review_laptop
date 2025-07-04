package com.koreaitsecurity.review.service;


import com.koreaitsecurity.review.dto.ApiRespDto;
import com.koreaitsecurity.review.dto.SendMailReqDto;
import com.koreaitsecurity.review.entity.User;
import com.koreaitsecurity.review.entity.UserRole;
import com.koreaitsecurity.review.repository.UserRepository;
import com.koreaitsecurity.review.repository.UserRoleRepository;
import com.koreaitsecurity.review.security.jwt.JwtUtil;
import com.koreaitsecurity.review.security.model.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class MailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public ApiRespDto<?> sendMail(SendMailReqDto sendMailReqDto, PrincipalUser principalUser) {
        if (!principalUser.getEmail().equals(sendMailReqDto.getEmail())) {
            //만약 메일이 일치하지 않는다면
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }

        // 이메일 유무 확인
        Optional<User> optionalUser = userRepository.getUserByEmail(sendMailReqDto.getEmail());

        if (optionalUser.isEmpty()) { // 유저가 비어있으면
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요.", null);
        }
        // 통과한 경우 : 인증이 완료됨

        User user = optionalUser.get();

        boolean hasTempRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleId() == 3); // userRoleList에 3번이 있는지
        // 유저가 임시 사용자 이면 true
        if (!hasTempRole) {
            return new ApiRespDto<>("failed", "인증이 필요한 계정이 아닙니다.",null);
        }

        // 로그인 할 때 인증 email 제작

        String token = jwtUtil.generateMailVerifyToken(user.getUserId().toString());
        // generateMailVerifyToken에서 string 으로 id를 받음

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail()); // 수신자 이메일
        message.setSubject("이메일 인증입니다."); // 제목
        message.setText("링크를 클릭해 인증을 완료해주세요. : " +
                "http://localhost:8080/mail/verify?verifyToken=" + token );
        // ?requestparam값 = xhlzms -> 서버로 받아서 확인후 임시 -> 일반 사용자로 변경

        // 메일 발송
        javaMailSender.send(message);

        return new ApiRespDto<>("success","인증 메일이 전송되었습니다. 메일을 확인하세요.", null);
    }


    public Map<String, Object> verify(String token) {
        Claims claims = null; // jwt의 payload영역
        Map<String, Object> resultMap = null;

        try {
            claims = jwtUtil.getClaims(token);
            String subject = claims.getSubject();

            if (!"VerifyRoken".equals(subject)) { // verifytoken 이 일치하지 않는다면
                resultMap = Map.of("status", "failed", "message", "잘못된 접근입니다.");
            }

            Integer userId = Integer.parseInt(claims.getId());
            Optional<User> optionalUser = userRepository.getUserByUserId(userId);

            if (optionalUser.isEmpty()) {
                resultMap = Map.of("status", "failed", "message", "존재하지 않는 사용자 입니다.");
            }

            Optional<UserRole> optionalUserRole = userRoleRepository.getUserRoleByUserIdAndRoleId(userId, 3);
            if (optionalUserRole.isEmpty()) { // userRole이 3번이 아니면
                resultMap = Map.of("status", "failed", "message", "이미 인증이 완료된 메일입니다.");
            } else {
                userRoleRepository.updateRoleId(userId, optionalUserRole.get().getUserRoleId());
                resultMap = Map.of("status", "success", "message", "이메일 인증이 완료되었습니다.");
            }
        } catch (ExpiredJwtException e) { // 만료된 토큰일때
            resultMap = Map.of("status", "success", "message", "만료된 인증 요청입니다. \n인증 메일을 다시 요청해주세요.");
        } catch (JwtException e) {
            resultMap = Map.of("status", "failed", "message", "잘못된 접근입니다.\n인증 메일을 다시 요청해주세요.");
        }
        return resultMap;
    }

}
