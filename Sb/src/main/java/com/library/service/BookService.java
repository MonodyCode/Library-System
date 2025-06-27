package com.library.service;

import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    
    /**
     * 添加图书
     */
    boolean addBook(Book book);
    
    /**
     * 根据ID查询图书
     */
    Book findById(Long id);
    
    /**
     * 更新图书信息
     */
    boolean updateBook(Book book);
    
    /**
     * 删除图书
     */
    boolean deleteBook(Long id);
    
    /**
     * 分页查询图书列表
     */
    PageInfo<Book> findBooksByPage(PageQuery pageQuery, String category, String author, String publisher);
    
    /**
     * 模糊搜索图书
     */
    List<Book> searchBooks(String keyword);
    
    /**
     * 获取所有分类
     */
    List<String> getAllCategories();
    
    /**
     * 统计图书数量
     */
    int countBooks();
    
    /**
     * 上传图书封面
     */
    String uploadCover(MultipartFile file);
    
    /**
     * 更新图书库存
     */
    boolean updateAvailableCount(Long id, Integer count);
    
    /**
     * 获取热门图书
     */
    List<Book> getPopularBooks(Integer limit);
    
    /**
     * 检查ISBN是否存在
     */
    boolean checkIsbnExists(String isbn);
}