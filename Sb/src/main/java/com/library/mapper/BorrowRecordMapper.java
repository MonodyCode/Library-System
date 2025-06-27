package com.library.mapper;

import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowRecordMapper {
    
    /**
     * 插入借阅记录
     */
    int insert(BorrowRecord borrowRecord);
    
    /**
     * 根据ID查询借阅记录
     */
    BorrowRecord findById(@Param("id") Long id);
    
    /**
     * 更新借阅记录
     */
    int update(BorrowRecord borrowRecord);
    
    /**
     * 分页查询借阅记录
     */
    List<BorrowRecord> findByPage(@Param("userId") Long userId, 
                                 @Param("status") Integer status,
                                 @Param("keyword") String keyword);
    
    /**
     * 查询用户当前借阅的图书
     */
    List<BorrowRecord> findCurrentBorrowsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户是否已借阅某本书
     */
    BorrowRecord findCurrentBorrow(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    /**
     * 归还图书
     */
    int returnBook(@Param("id") Long id);
    
    /**
     * 统计借阅记录数量
     */
    int countBorrowRecords(@Param("status") Integer status);
    
    /**
     * 查询逾期记录
     */
    List<BorrowRecord> findOverdueRecords();
    
    /**
     * 统计图表数据
     */
    List<Map<String, Object>> getBorrowStatistics(@Param("days") Integer days);
    
    /**
     * 获取热门图书借阅统计
     */
    List<Map<String, Object>> getPopularBooksStatistics(@Param("limit") Integer limit);
}