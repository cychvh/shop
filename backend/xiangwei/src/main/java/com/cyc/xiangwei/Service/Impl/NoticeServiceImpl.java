package com.cyc.xiangwei.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.xiangwei.Service.NoticeService;
import com.cyc.xiangwei.Service.UserService;
import com.cyc.xiangwei.bean.Notice;
import com.cyc.xiangwei.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public IPage<Notice> getNoticeList(Integer pageNum, Integer pageSize, Integer type) {
        IPage<Notice> page = new Page<>(pageNum, pageSize);
        if(type == 1){
            return baseMapper.selectByType(page,"Merchant");
        }else if(type == 2){
            return baseMapper.selectByType(page,"User");
        }
        return null;
    }
}
