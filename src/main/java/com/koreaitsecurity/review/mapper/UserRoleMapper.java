package com.koreaitsecurity.review.mapper;

import com.koreaitsecurity.review.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    int insert(UserRole userRole);
}
