package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    
    /**
     * 插入图书
     */
    int insert(Book book);
    
    /**
     * 根据ID查询图书
     */
    Book findById(@Param("id") Long id);
    
    /**
     * 更新图书信息
     */
    int update(Book book);
    
    /**
     * 删除图书
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询图书列表
     */
    List<Book> findByPage(@Param("keyword") String keyword, 
                         @Param("category") String category,
                         @Param("author") String author,
                         @Param("publisher") String publisher);
    
    /**
     * 根据ISBN查询图书
     */
    Book findByIsbn(@Param("isbn") String isbn);
    
    /**
     * 模糊查询图书
     */
    List<Book> searchBooks(@Param("keyword") String keyword);
    
    /**
     * 获取所有分类
     */
    List<String> getAllCategories();
    
    /**
     * 统计图书数量
     */
    int countBooks();
    
    /**
     * 更新图书库存
     */
    int updateAvailableCount(@Param("id") Long id, @Param("count") Integer count);
    
    /**
     * 获取热门图书
     */
    List<Book> getPopularBooks(@Param("limit") Integer limit);
}