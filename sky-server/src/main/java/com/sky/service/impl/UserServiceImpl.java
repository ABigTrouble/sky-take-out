package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登陆
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO){
        String openId = getOpenId(userLoginDTO);
        // 检查是否已有该用户
        User user = userMapper.getByOpenId(openId);
        // 不存在该用户
        if(user ==null){
            // 注册
            user = new User();
            user.setOpenid(openId);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }
        // 有则返回
        return user;
    }

    private String getOpenId(UserLoginDTO userLoginDTO){
        // 调用微信的接口获取openId
        Map<String, String> map = new HashMap();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String res = HttpClientUtil.doGet(WX_LOGIN_URL, map); // 返回的是json的字符串
        log.info("微信返回的信息：{}",res);
        //解析json字符串，取出openid
        JSONObject jsonObject = JSON.parseObject(res);
        String openId = jsonObject.getString("openid");
        if(openId == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        log.info("获取到的openId: {}",openId);
        return openId;
    }
}
