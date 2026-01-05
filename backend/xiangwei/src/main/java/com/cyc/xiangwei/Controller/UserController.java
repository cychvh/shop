package com.cyc.xiangwei.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.Config.JWTConfig;
import com.cyc.xiangwei.Service.UserService;
import com.cyc.xiangwei.bean.Result;
import com.cyc.xiangwei.bean.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTConfig jwtConfig;

    @PostMapping("/user/login")
    public Result<?> login(@RequestBody User user) {
        User Formuser = userService.Login(user.getUsername(), user.getPassword());
        if (Formuser == null) {
            return Result.error("10086","登入失败");
        }
//        System.out.println(Formuser);
        Formuser.setPassword(null);
        Map<String,Object> map = new HashMap<>();
        map.put("token",jwtConfig.generateToken(Formuser.getUsername()));
        map.put("user",Formuser);
        return Result.success(map);
    }
    @PostMapping("/user/register")
    public Result<?> Register(@RequestBody User user) {
        boolean register = userService.Register(user);
        if (register) {

            return Result.success();
        }
        return Result.error("10086","用户名已被使用");
    }
    @GetMapping("/user/getuser")
    public Result<?> getUser(@RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "5") int size,
                             @RequestParam(defaultValue = "") String search,
                             HttpServletRequest request) {
        Integer type = (Integer) request.getAttribute("type");
        if (type == null || type != 0) {
            return Result.error("405","权限不足");
        }else {
            LambdaQueryWrapper<User>lambdaQueryWrapper = new LambdaQueryWrapper<>();
            if(StringUtils.hasText(search)){
                lambdaQueryWrapper.like(User::getUsername,search);
            }
            System.out.println(size);
            Page<User> page = userService.page(new Page<>(pageNum, size), lambdaQueryWrapper);
            return Result.success(page);
        }
    }

    @PutMapping("/user/updateUser")
    public Result<?>  updateUser(@RequestBody User user,HttpServletRequest request) {
        Integer type = (Integer)request.getAttribute("type");
        Integer userId = (Integer)request.getAttribute("userId");
        if (type == null || userId == null) {
            return Result.error("401", "未登录");
        }
        if(type !=0 && !userId.equals(user.getId())){
            return Result.error("405","权限不够");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());

        if (type == 0) {
            // 管理员
            updateUser.setUsername(user.getUsername());
            updateUser.setType(user.getType());

        } else {
            // 普通用户
            updateUser.setAddress(user.getAddress());
            updateUser.setPassword(user.getPassword());
        }
        boolean b = userService.updateById(updateUser);
        if(b){
            return Result.success();
        }else {
            return Result.error("500","修改失败");
        }
    }
    @DeleteMapping("/user/{id}")
    public Result<?> delete(@PathVariable("id") Integer id,HttpServletRequest request) {
        Integer type = (Integer)request.getAttribute("type");
        if(type == 0){
            if (userService.removeById(id)) {
                return Result.success();
            }
            return Result.error("500","删除失败");
        }else {
            return Result.error("405","权限不足");
        }
    }
}
