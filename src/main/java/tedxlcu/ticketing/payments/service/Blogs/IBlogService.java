package tedxlcu.ticketing.payments.service.Blogs;

import java.util.List;

import tedxlcu.ticketing.payments.Request.createBlogRequest;
import tedxlcu.ticketing.payments.model.Blog;

public interface IBlogService {
  // Define method signatures for blog operations
  void createBlog(createBlogRequest blog);
  Blog getBlogById(String id);
  List<Blog> getAllBlogs();
  void updateBlog(String id, createBlogRequest blog);
  void deleteBlog(String id);
}
