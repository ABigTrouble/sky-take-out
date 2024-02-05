package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public Result<UserLoginVO> wxLogin(@RequestBody UserLoginDTO userLoginDTO){
        // 根据code查询对应的User
        log.info("用户登录: {}", userLoginDTO.getCode());
        User user = userService.wxLogin(userLoginDTO);

        // 组装VO
        // 组装jwt
        Map<String, Object> claims = new HashMap();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO vo = UserLoginVO.builder()
                .id(user.getId())
                .token(jwt)
                .openid(user.getOpenid()).build();
        return Result.success(vo);

    }
}
