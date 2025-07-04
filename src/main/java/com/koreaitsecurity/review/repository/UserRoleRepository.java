package com.koreaitsecurity.review.repository;

import com.koreaitsecurity.review.entity.UserRole;
import com.koreaitsecurity.review.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public Optional<UserRole> addUserRole(UserRole userRole) {
        return userRoleMapper.insert(userRole) < 1 ? // 1보다 아래면 - 영향받은 행
        Optional.empty() : Optional.of(userRole); // 아니면 변수에 userRole 넣어줌
    }

    public Optional<UserRole>getUserRoleByUserIdAndRoleId(Integer userId, Integer roleId) {
        return userRoleMapper.getUserRoleByUserIdAndRoleId(userId, roleId);
    }

    public int updateRoleId(Integer userId, Integer userRoleId) {
        return userRoleMapper.updateRoleId(userId, userRoleId);
    }
}
