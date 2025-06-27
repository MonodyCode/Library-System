package com.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Override
    @Transactional
    public boolean borrowBook(Long userId, Long bookId, Integer borrowDays) {
        // 检查用户是否已借阅该书
        if (checkUserBorrowedBook(userId, bookId)) {
            return false;
        }
        
        // 检查图书库存
        Book book = bookMapper.findById(bookId);
        if (book == null || book.getAvailableCount() <= 0) {
            return false;
        }
        
        // 计算归还日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, borrowDays);
        Date dueTime = calendar.getTime();
        
        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord(userId, bookId, dueTime);
        
        // 插入借阅记录
        if (borrowRecordMapper.insert(borrowRecord) > 0) {
            // 更新图书库存
            return bookMapper.updateAvailableCount(bookId, book.getAvailableCount() - 1) > 0;
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public boolean returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null || record.getStatus() != 1) {
            return false;
        }
        
        // 更新借阅记录状态
        record.setStatus(2);
        record.setReturnTime(new Date());
        
        if (borrowRecordMapper.update(record) > 0) {
            // 更新图书库存
            Book book = bookMapper.findById(record.getBookId());
            return bookMapper.updateAvailableCount(record.getBookId(), book.getAvailableCount() + 1) > 0;
        }
        
        return false;
    }
    
    @Override
    public BorrowRecord findById(Long id) {
        return borrowRecordMapper.findById(id);
    }
    
    @Override
    public PageInfo<BorrowRecord> findRecordsByPage(PageQuery pageQuery, Long userId, Integer status) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<BorrowRecord> records = borrowRecordMapper.findByPage(userId, status, pageQuery.getKeyword());
        return new PageInfo<>(records);
    }
    
    @Override
    public List<BorrowRecord> findCurrentBorrowsByUserId(Long userId) {
        return borrowRecordMapper.findCurrentBorrowsByUserId(userId);
    }
    
    @Override
    public boolean checkUserBorrowedBook(Long userId, Long bookId) {
        return borrowRecordMapper.findCurrentBorrow(userId, bookId) != null;
    }
    
    @Override
    public int countBorrowRecords(Integer status) {
        return borrowRecordMapper.countBorrowRecords(status);
    }
    
    @Override
    public List<BorrowRecord> findOverdueRecords() {
        return borrowRecordMapper.findOverdueRecords();
    }
    
    @Override
    @Transactional
    public void updateOverdueStatus() {
        List<BorrowRecord> overdueRecords = findOverdueRecords();
        for (BorrowRecord record : overdueRecords) {
            if (record.getStatus() == 1) {
                record.setStatus(3); // 设置为逾期状态
                borrowRecordMapper.update(record);
            }
        }
    }
    
    @Override
    public List<Map<String, Object>> getBorrowStatistics(Integer days) {
        return borrowRecordMapper.getBorrowStatistics(days);
    }
    
    @Override
    public List<Map<String, Object>> getPopularBooksStatistics(Integer limit) {
        return borrowRecordMapper.getPopularBooksStatistics(limit);
    }
}