package com.library.controller;

import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "*")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    /**
     * 添加图书（管理员功能）
     */
    @PostMapping("/add")
    public Result<String> addBook(@RequestBody Book book, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (bookService.checkIsbnExists(book.getIsbn())) {
            return Result.error("ISBN已存在");
        }
        
        if (bookService.addBook(book)) {
            return Result.success("图书添加成功");
        } else {
            return Result.error("图书添加失败");
        }
    }
    
    /**
     * 根据ID查询图书
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            return Result.success(book);
        } else {
            return Result.error("图书不存在");
        }
    }
    
    /**
     * 更新图书信息（管理员功能）
     */
    @PutMapping("/update")
    public Result<String> updateBook(@RequestBody Book book, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (bookService.updateBook(book)) {
            return Result.success("图书更新成功");
        } else {
            return Result.error("图书更新失败");
        }
    }
    
    /**
     * 删除图书（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteBook(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (bookService.deleteBook(id)) {
            return Result.success("图书删除成功");
        } else {
            return Result.error("图书删除失败");
        }
    }
    
    /**
     * 分页查询图书列表
     */
    @GetMapping("/list")
    public Result<PageInfo<Book>> getBookList(PageQuery pageQuery,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(required = false) String author,
                                             @RequestParam(required = false) String publisher) {
        PageInfo<Book> pageInfo = bookService.findBooksByPage(pageQuery, category, author, publisher);
        return Result.success(pageInfo);
    }
    
    /**
     * 搜索图书
     */
    @GetMapping("/search")
    public Result<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return Result.success(books);
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        List<String> categories = bookService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 上传图书封面（管理员功能）
     */
    @PostMapping("/upload-cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }
        
        String filePath = bookService.uploadCover(file);
        if (filePath != null) {
            return Result.success("上传成功", filePath);
        } else {
            return Result.error("上传失败");
        }
    }
    
    /**
     * 获取热门图书
     */
    @GetMapping("/popular")
    public Result<List<Book>> getPopularBooks(@RequestParam(defaultValue = "10") Integer limit) {
        List<Book> books = bookService.getPopularBooks(limit);
        return Result.success(books);
    }
}