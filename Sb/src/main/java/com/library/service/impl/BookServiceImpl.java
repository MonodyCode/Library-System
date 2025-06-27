package com.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookMapper bookMapper;
    
    private static final String UPLOAD_PATH = "uploads/covers/";
    
    @Override
    @CacheEvict(value = "books", allEntries = true)
    public boolean addBook(Book book) {
        // 检查ISBN是否已存在
        if (checkIsbnExists(book.getIsbn())) {
            return false;
        }
        
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());
        book.setAvailableCount(book.getTotalCount());
        
        return bookMapper.insert(book) > 0;
    }
    
    @Override
    @Cacheable(value = "books", key = "#id")
    public Book findById(Long id) {
        return bookMapper.findById(id);
    }
    
    @Override
    @CacheEvict(value = "books", key = "#book.id")
    public boolean updateBook(Book book) {
        book.setUpdateTime(new Date());
        return bookMapper.update(book) > 0;
    }
    
    @Override
    @CacheEvict(value = "books", allEntries = true)
    public boolean deleteBook(Long id) {
        return bookMapper.deleteById(id) > 0;
    }
    
    @Override
    public PageInfo<Book> findBooksByPage(PageQuery pageQuery, String category, String author, String publisher) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<Book> books = bookMapper.findByPage(pageQuery.getKeyword(), category, author, publisher);
        return new PageInfo<>(books);
    }
    
    @Override
    public List<Book> searchBooks(String keyword) {
        return bookMapper.searchBooks(keyword);
    }
    
    @Override
    @Cacheable(value = "categories")
    public List<String> getAllCategories() {
        return bookMapper.getAllCategories();
    }
    
    @Override
    public int countBooks() {
        return bookMapper.countBooks();
    }
    
    @Override
    public String uploadCover(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(UPLOAD_PATH);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            
            return UPLOAD_PATH + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    @CacheEvict(value = "books", key = "#id")
    public boolean updateAvailableCount(Long id, Integer count) {
        return bookMapper.updateAvailableCount(id, count) > 0;
    }
    
    @Override
    @Cacheable(value = "popularBooks", key = "#limit")
    public List<Book> getPopularBooks(Integer limit) {
        return bookMapper.getPopularBooks(limit);
    }
    
    @Override
    public boolean checkIsbnExists(String isbn) {
        return bookMapper.findByIsbn(isbn) != null;
    }
}