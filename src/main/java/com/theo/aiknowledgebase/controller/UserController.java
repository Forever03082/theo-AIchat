package com.theo.aiknowledgebase.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.theo.aiknowledgebase.dto.Result;
import com.theo.aiknowledgebase.entity.User;
import com.theo.aiknowledgebase.mapper.UserMapper;
import com.theo.aiknowledgebase.util.JwtUtil;
import com.theo.aiknowledgebase.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 1. 注册接口（包含邮箱验证、密码非空断言）
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        // A. 基础断言：防止空值或全是空格
        if (!StringUtils.hasText(username)) {
            return Result.error(400, "注册失败：邮箱用户名不能为空");
        }
        if (!StringUtils.hasText(password)) {
            return Result.error(400, "注册失败：密码不能为空");
        }

        // B. 🌟 核心：后端强效邮箱正则过滤
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailRegex, username)) {
            return Result.error(400, "注册失败：用户名格式不正确，必须是标准电子邮箱");
        }

        // C. 安全排重：防止重复注册
        User existingUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );
        if (existingUser != null) {
            return Result.error(400, "注册失败：该邮箱账号已存在");
        }

        // D. 密码加密
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);

        // E. 写入数据库
        userMapper.insert(user);
        return Result.success("注册成功", null);
    }

    /**
     * 2. 登录接口（包含基础断言及统一包装）
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error(400, "登录失败：账号或密码不能为空");
        }

        // 1. 根据用户名查询数据库
        User dbUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        // 2. 用户不存在或密码不匹配
        if (dbUser == null || !BCrypt.checkpw(password, dbUser.getPassword())) {
            return Result.error(400, "登录失败：邮箱用户名或密码错误");
        }

        // 3. 签发安全通行凭证
        String token = jwtUtil.createToken(username);
        return Result.success("登录成功", Map.of("token", token));
    }
}