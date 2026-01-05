package com.cyc.xiangwei.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.Service.NoticeService;
import com.cyc.xiangwei.bean.Notice;
import com.cyc.xiangwei.bean.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

//公告控制层
@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    //这个是用用户和商家的
    @GetMapping("/notice/getNotice")
    public Result<?> getNotice(HttpServletRequest request,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "5") Integer pageSize) {
        Integer type = (Integer)request.getAttribute("type");
        if (type == null) {
            return Result.error("401", "未登录");
        }
        IPage<Notice> noticeList = noticeService.getNoticeList(pageNum, pageSize, type);
        return Result.success(noticeList);
    }
    @GetMapping("/notice/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "5") Integer pageSize,
                          @RequestParam(defaultValue = "")String search,
                          HttpServletRequest request) {
        Integer type = (Integer) request.getAttribute("type");
        if (type == null || type != 0) {
            return Result.error("405", "权限不够");
        }
        LambdaQueryWrapper<Notice> noticeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) {
            noticeLambdaQueryWrapper.like(Notice::getContent, search);
        }
        Page<Notice> page = noticeService.page(new Page<>(pageNum, pageSize), noticeLambdaQueryWrapper);
        return Result.success(page);
    }
    @PostMapping("/notice/addNotice")
    public Result<?> addNotice(@RequestBody Notice notice,HttpServletRequest request) {
        Integer type = (Integer) request.getAttribute("type");
        if (type == 0) {
            notice.setId(null);
            notice.setDate(LocalDateTime.now());
            boolean save = noticeService.save(notice);
            if(save) {
                return Result.success();
            }
            return Result.error("500","添加失败");
        }else {
            return Result.error("405","权限不够");
        }
    }
    @DeleteMapping("/notice/deleteNotice")
    public Result<?> deleteNotice(@RequestParam Integer id,HttpServletRequest request) {
        Integer type = (Integer) request.getAttribute("type");
        if (type == 0) {
            if (id == null) {
                return Result.error("400", "公告ID不能为空");
            }
            boolean b = noticeService.removeById(id);
            if(b){
                return Result.success();
            }
            return Result.error("405","删除失败");
        }else {
            return Result.error("405","权限不够");
        }
    }
    @PutMapping("/notice/updateNotice")
    public Result<?> updateNotice(@RequestBody Notice notice,HttpServletRequest request) {
        Integer type = (Integer) request.getAttribute("type");
        if (type == 0) {
            notice.setDate(LocalDateTime.now());
            boolean b = noticeService.updateById(notice);
            if(b){
                return Result.success();
            }
            return Result.error("406","修改失败");
        }else{
            return Result.error("405","权限不够");
        }
    }
}
