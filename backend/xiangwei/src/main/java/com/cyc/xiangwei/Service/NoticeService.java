package com.cyc.xiangwei.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.xiangwei.bean.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice> {
    public IPage<Notice> getNoticeList(Integer pageNum,Integer pageSize,Integer type);
}
