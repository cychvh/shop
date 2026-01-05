package com.cyc.xiangwei.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.xiangwei.bean.User;

public interface UserService extends IService<User> {
    User Login(String username, String password);
    boolean Register(User user);
    int GetType (String Username);

}
