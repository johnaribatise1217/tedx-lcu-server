package tedxlcu.ticketing.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tedxlcu.ticketing.payments.Request.createBlogRequest;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.service.Blogs.IBlogService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/blogs")
public class BlogController {
  @Autowired
  private IBlogService blogService;
  
  // Define endpoints for blog operations (create, read, update, delete)
  @PostMapping("/create")
  public ResponseEntity<ApiResponse> createBlog(createBlogRequest blogRequest) {
    // Implementation for creating a blog
    blogService.createBlog(blogRequest); // Replace null with actual blog data
    return ResponseEntity.status(HttpStatus.CREATED).body(
      new ApiResponse(true, "201", "Blog created successfully", null)
    );
  }

  // Other endpoints (get, update, delete) can be defined similarly
  @GetMapping("/all")
  public ResponseEntity<ApiResponse> getAllBlogs() {
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Blogs retrieved successfully", blogService.getAllBlogs())
    );
  }

  @GetMapping("/view/{id}")
  public ResponseEntity<ApiResponse> getBlogById(@PathVariable String id) {
    return ResponseEntity.ok(
      new ApiResponse(
        true, "200", "Blog retrieved successfully", blogService.getBlogById(id)
      )
    );
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<ApiResponse> updateBlog(
    @PathVariable String id,
    createBlogRequest blogRequest
  ) {
    blogService.updateBlog(id, blogRequest);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Blog updated successfully", null)
    );
  }

  // Delete blog endpoint
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ApiResponse> deleteBlog(@PathVariable String id) {
    blogService.deleteBlog(id);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Blog deleted successfully", null)
    ); 
  }
}
