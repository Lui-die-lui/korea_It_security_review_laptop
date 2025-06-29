package com.koreaitsecurity.review.repository;

import com.koreaitsecurity.review.entity.User;
import com.koreaitsecurity.review.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;



    public Optional<User> addUser(User user) {
//        return userMapper.addUser(user);
        try {
            userMapper.addUser(user);
        } catch (DuplicateKeyException e) { // 유저 아이디가 중복일때 생기는 예외
          return Optional.empty(); // 없다고 빈 껍데기 반환
        }
        return Optional.of(user); // user는 매개변수로 가져온 애
        // 여기서 등록한 유저 아이디를 유저 객체에 넣음 - 그러고 저 객체 안에 다시 리턴함
    }

    public Optional<User> getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }
}
