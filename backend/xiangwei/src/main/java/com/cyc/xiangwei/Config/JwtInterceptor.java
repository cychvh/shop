package com.cyc.xiangwei.Config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyc.xiangwei.Service.UserService;
import com.cyc.xiangwei.bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final long REDIS_EXPIRE_SECONDS = 30;

    @Autowired
    private JWTConfig jwtConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 放行 OPTIONS / 登录 / 注册
        String uri = request.getRequestURI();
        if ("OPTIONS".equals(request.getMethod())
                || uri.contains("/user/login")
                || uri.contains("/user/register")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        token = token.substring(7);

        // Token 校验失败
        if (!jwtConfig.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String username = jwtConfig.getUsernameFromToken(token);
        String redisKey = "login:user:" + username;

        // 先查 Redis
        User user = (User) redisTemplate.opsForValue().get(redisKey);
        if (user == null) {
            // Redis 不存在，查数据库
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            user = userService.getOne(wrapper);

            if (user == null) {
                // Token 合法但用户不存在
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 存 Redis，设置 30 秒有效期
            redisTemplate.opsForValue()
                    .set(redisKey, user, REDIS_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }

        // 设置请求上下文
        request.setAttribute("userId", user.getId());
        request.setAttribute("type", user.getType());
        request.setAttribute("username", username);

        return true;
    }
}
