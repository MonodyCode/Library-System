package com.library.service;

import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.entity.BorrowRecord;

import java.util.List;
import java.util.Map;

public interface BorrowRecordService {
    
    /**
     * 借阅图书
     */
    boolean borrowBook(Long userId, Long bookId, Integer borrowDays);
    
    /**
     * 归还图书
     */
    boolean returnBook(Long recordId);
    
    /**
     * 根据ID查询借阅记录
     */
    BorrowRecord findById(Long id);
    
    /**
     * 分页查询借阅记录
     */
    PageInfo<BorrowRecord> findRecordsByPage(PageQuery pageQuery, Long userId, Integer status);
    
    /**
     * 查询用户当前借阅的图书
     */
    List<BorrowRecord> findCurrentBorrowsByUserId(Long userId);
    
    /**
     * 检查用户是否已借阅某本书
     */
    boolean checkUserBorrowedBook(Long userId, Long bookId);
    
    /**
     * 统计借阅记录数量
     */
    int countBorrowRecords(Integer status);
    
    /**
     * 查询逾期记录
     */
    List<BorrowRecord> findOverdueRecords();
    
    /**
     * 更新逾期状态
     */
    void updateOverdueStatus();
    
    /**
     * 获取借阅统计数据
     */
    List<Map<String, Object>> getBorrowStatistics(Integer days);
    
    /**
     * 获取热门图书借阅统计
     */
    List<Map<String, Object>> getPopularBooksStatistics(Integer limit);
}