package com.koreaitsecurity.review.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koreaitsecurity.review.entity.UserRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String username;
    @JsonIgnore //  패스워드는 받기만 하고 주는 경우는 없음 - 보안적인 위험때문에
    private String password;
    private String email;
    private List<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 권한 반환
        return userRoles.stream().map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                // userRole 안의 username을 가져옴(getRoleName) - new 객체륾 만듦
                .collect(Collectors.toList()); // 유저 이름들을 collector 로 만듦
        // map = collection 자료형에 있는 요소에다가 하나하나 어떠한 작업들을(리스트 안에 들어있는) 적용시키는것
    }
}
