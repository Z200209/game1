package com.example.module.service;

import com.example.module.entity.User;
import com.example.module.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserById(BigInteger id) {
        return userMapper.getUserById(id);
    }

    @Transactional
    public int insert(User user) {
        return userMapper.insert(user);
    }
    
    @Transactional
    public int update(User user) {
        return userMapper.update(user);
    }
    
    public User getUserByPhone(String phone) {
        if (phone == null){
            throw new RuntimeException("手机号不能为空");
        }
        return userMapper.getUserByPhone(phone);
    }

    public User login(String phone, String password) {
        if (phone == null || password == null){
            throw new RuntimeException("手机号或密码不能为空");
        }
        User user = userMapper.getUserByPhone(phone);
        if (user == null){
            throw new RuntimeException("手机号不存在");
        }
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())){
            throw new RuntimeException("密码错误");
        }
        return user;
    }
    
    @Transactional
    public int register(String phone, String password,String name, String avatar)
    {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (phone == null || password == null || name == null || avatar == null){
            throw new RuntimeException("参数不能为空");
        }
        if (userMapper.getUserByPhone(phone)!=null){
            throw new RuntimeException("手机号已存在");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        User user = new User().setPhone(phone)
                .setPassword(password)
                .setName(name)
                .setAvatar(avatar)
                .setCreateTime(time)
                .setUpdateTime(time);

        System.out.println(user);
        int result = insert(user);
        if (result == 0){
            throw new RuntimeException("注册失败");
        }
        return result;

    }
    
    @Transactional
    public int updateInfo (BigInteger id,String phone, String password, String name, String avatar) {
        int time = (int) (System.currentTimeMillis() / 1000);

        if (id == null || phone == null || password == null || name == null || avatar == null){
            throw new RuntimeException("参数不能为空");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        User user = new User().setName(name)
                .setAvatar(avatar)
                .setPhone(phone)
                .setPassword(password)
                .setId(id)
                .setUpdateTime(time);
        int result = update(user);
        if (result == 0){
            throw new RuntimeException("更新失败");
        }
        return result;
    }
}
