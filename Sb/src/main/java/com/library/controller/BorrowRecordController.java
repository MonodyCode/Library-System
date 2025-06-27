package com.library.controller;

import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow")
@CrossOrigin(origins = "*")
public class BorrowRecordController {
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    /**
     * 借阅图书
     */
    @PostMapping("/borrow")
    public Result<String> borrowBook(@RequestParam Long bookId,
                                   @RequestParam(defaultValue = "30") Integer borrowDays,
                                   HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (borrowRecordService.checkUserBorrowedBook(currentUser.getId(), bookId)) {
            return Result.error("您已借阅该图书，请先归还后再借阅");
        }
        
        if (borrowRecordService.borrowBook(currentUser.getId(), bookId, borrowDays)) {
            return Result.success("借阅成功");
        } else {
            return Result.error("借阅失败，图书库存不足或其他原因");
        }
    }
    
    /**
     * 归还图书
     */
    @PostMapping("/return/{recordId}")
    public Result<String> returnBook(@PathVariable Long recordId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        BorrowRecord record = borrowRecordService.findById(recordId);
        if (record == null) {
            return Result.error("借阅记录不存在");
        }
        
        // 检查权限：只能归还自己的书或管理员可以操作任何记录
        if (!currentUser.getId().equals(record.getUserId()) && currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (borrowRecordService.returnBook(recordId)) {
            return Result.success("归还成功");
        } else {
            return Result.error("归还失败");
        }
    }
    
    /**
     * 分页查询借阅记录
     */
    @GetMapping("/list")
    public Result<PageInfo<BorrowRecord>> getBorrowRecords(PageQuery pageQuery,
                                                          @RequestParam(required = false) Long userId,
                                                          @RequestParam(required = false) Integer status,
                                                          HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        // 普通用户只能查看自己的借阅记录
        if (currentUser.getUserType() == 1) {
            userId = currentUser.getId();
        }
        
        PageInfo<BorrowRecord> pageInfo = borrowRecordService.findRecordsByPage(pageQuery, userId, status);
        return Result.success(pageInfo);
    }
    
    /**
     * 查询当前用户的借阅记录
     */
    @GetMapping("/current")
    public Result<List<BorrowRecord>> getCurrentBorrows(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        List<BorrowRecord> records = borrowRecordService.findCurrentBorrowsByUserId(currentUser.getId());
        return Result.success(records);
    }
    
    /**
     * 查询逾期记录（管理员功能）
     */
    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> getOverdueRecords(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        List<BorrowRecord> records = borrowRecordService.findOverdueRecords();
        return Result.success(records);
    }
    
    /**
     * 更新逾期状态（管理员功能）
     */
    @PostMapping("/update-overdue")
    public Result<String> updateOverdueStatus(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        borrowRecordService.updateOverdueStatus();
        return Result.success("逾期状态更新成功");
    }
    
    /**
     * 获取借阅统计数据（管理员功能）
     */
    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> getBorrowStatistics(@RequestParam(defaultValue = "30") Integer days,
                                                                HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        List<Map<String, Object>> statistics = borrowRecordService.getBorrowStatistics(days);
        return Result.success(statistics);
    }
    
    /**
     * 获取热门图书借阅统计（管理员功能）
     */
    @GetMapping("/popular-books")
    public Result<List<Map<String, Object>>> getPopularBooksStatistics(@RequestParam(defaultValue = "10") Integer limit,
                                                                       HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        List<Map<String, Object>> statistics = borrowRecordService.getPopularBooksStatistics(limit);
        return Result.success(statistics);
    }
}