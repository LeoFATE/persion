package com.example.mapper;



import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {


    // 根据用户名查询用户
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    List<User> getUserByName(String e_name);

}
