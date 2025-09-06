package tedxlcu.ticketing.payments.service.Blogs;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tedxlcu.ticketing.payments.Exception.ResourceNotFoundException;
import tedxlcu.ticketing.payments.Request.createBlogRequest;
import tedxlcu.ticketing.payments.model.Blog;
import tedxlcu.ticketing.payments.repository.BlogRepository;

@Service
public class BlogService implements IBlogService {
  @Autowired
  private BlogRepository blogRepository;

  @Override
  public void createBlog(createBlogRequest blog) {
    Blog newBlog = new Blog();
    newBlog.setTitle(blog.getTitle());
    newBlog.setSubTitle(blog.getSubTitle());
    newBlog.setNumParagraphs(blog.getNumParagraphs());

    if(blog.getParagraphs().size() > blog.getNumParagraphs()){
      throw new IllegalArgumentException("Number of paragraphs exceeds the specified limit");
    }

    newBlog.setParagraphs(blog.getParagraphs());
    newBlog.setThumbnailUrl(blog.getThumbnailUrl());
    newBlog.setAuthor(blog.getAuthor());
    newBlog.setAuthorImageUrl(blog.getAuthorImageUrl());
    newBlog.setAuthorBio(blog.getAuthorBio());
    newBlog.setTags(blog.getTags());
    newBlog.setReadTime(blog.getReadTime());
    newBlog.setBlogImages(blog.getBlogImages());
    newBlog.setCreatedBy(blog.getCreatedBy());
    newBlog.setUpdatedBy(blog.getUpdatedBy());

    blogRepository.save(newBlog);
  }

  @Override
  public Blog getBlogById(String id) {
    return blogRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
  }

  @Override
  public List<Blog> getAllBlogs() {
    return blogRepository.findAll();
  }

  @Override
  public void updateBlog(String id, createBlogRequest blog) {
    Optional<Blog> existingBlogOpt = blogRepository.findById(id);
    if (existingBlogOpt.isPresent()) {
      Blog existingBlog = existingBlogOpt.get();
      existingBlog.setTitle(blog.getTitle());
      existingBlog.setSubTitle(blog.getSubTitle());
      existingBlog.setNumParagraphs(blog.getNumParagraphs());
      existingBlog.setParagraphs(blog.getParagraphs());
      existingBlog.setThumbnailUrl(blog.getThumbnailUrl());
      existingBlog.setAuthor(blog.getAuthor());
      existingBlog.setAuthorImageUrl(blog.getAuthorImageUrl());
      existingBlog.setAuthorBio(blog.getAuthorBio());
      existingBlog.setTags(blog.getTags());
      existingBlog.setReadTime(blog.getReadTime());
      existingBlog.setBlogImages(blog.getBlogImages());
      existingBlog.setUpdatedBy(blog.getUpdatedBy());

      blogRepository.save(existingBlog);
    } else {
      throw new ResourceNotFoundException("Blog not found with id: " + id);
    }
  }

  @Override
  public void deleteBlog(String id) {
    if (blogRepository.existsById(id)) {
      blogRepository.deleteById(id);
    } else {
      throw new ResourceNotFoundException("Blog not found with id: " + id);
    }
  }
}
