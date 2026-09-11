package com.music.config;

import com.music.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {//说明这个类是一个 Spring MVC 拦截器。拦截器可以在 Controller 执行之前/之后做一些事情。

    private final JwtUtil jwtUtil;
    //构造器注入
    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {//跨域的时候浏览器有时候会先发一个 OPTIONS 预检请求
            return true;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();
        // 以下路径无需认证
        // 登录注册
        if (path.contains("/api/user/login") || path.contains("/api/user/register")) {
            return true;//表示放行
        }
        // 音乐查询公开
        if (path.startsWith("/api/music/")
                && ("GET".equals(method) || "OPTIONS".equals(method))) {
            //用户不登录也可以搜索、查看、播放音乐。
            //但是：收藏歌曲创建歌单查看自己的播放历史这些涉及用户数据的接口，就应该要求登录。
            return true;
        }

        //正式验证 JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {//请求根本没有 Authorization或者Authorization 不是以 Bearer 开头的
            response.setStatus(401);
            return false;
        }
        //截取出 token 部分,从第 8 个字符开始截取。(前面7个是 "Bearer ")
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {//如果 token 无效
            response.setStatus(401);
            return false;
        }

        // 将用户ID存入request属性
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        return true;
    }
}

/*
客户端请求
    ↓
HttpServletRequest
    │├── Header
    │      └── Authorization
    │└── Attribute
*/