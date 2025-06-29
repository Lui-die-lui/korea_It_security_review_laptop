package com.koreaitsecurity.review.mapper;

import com.koreaitsecurity.review.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    int addUser(User user);
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByUsername(String username); // username을 넘겨 user를 반환함(signin)
}
