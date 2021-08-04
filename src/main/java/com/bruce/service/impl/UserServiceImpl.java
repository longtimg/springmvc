package com.bruce.service.impl;

import com.bruce.pojo.User;
import com.bruce.service.UserService;
import com.springmvc.annotation.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<User> findUser(String username) {
        List<User> list = new ArrayList<User>();
        list.add(new User("zhuifeng","22"));
        return list;
    }
}
