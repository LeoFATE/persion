package com.example.service;

import com.example.mapper.UserMapper;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getTheUserInfo(String username){

        int	x=4;
        System.out.println("value	is	"+ ((x>4) ? 99.9:9));

        List<User> userList = userMapper.getUserByName(username);
        if (userList.size() == 0){
            return new User();
        }else {
            return userList.get(0);
        }

    }

}
