package com.theo.aiknowledgebase.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.theo.aiknowledgebase.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 放行登录和注册
        String path = request.getRequestURI();
        if (path.contains("/login") || path.contains("/register")) {
            return true;
        }

        // 获取token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            // 检查请求是否希望得到一个HTML页面
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null && acceptHeader.contains("text/html")) {
                // 如果是页面请求，重定向到登录页
                response.sendRedirect(request.getContextPath() + "/login.html");
            } else {
                // 如果是API请求，返回401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"未登录或认证失败\"}");
            }
            return false;
        }

        try {
            String username = jwtUtil.getUsername(token);
            request.setAttribute("username", username);
        } catch (JWTVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"token无效\"}");
            return false;
        }

        return true;
    }
}