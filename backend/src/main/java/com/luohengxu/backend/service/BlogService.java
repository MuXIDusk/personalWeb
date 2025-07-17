package com.luohengxu.backend.service;

import com.luohengxu.backend.dto.BlogPostRequest;
import com.luohengxu.backend.dto.BlogPostResponse;
import com.luohengxu.backend.entity.BlogPost;
import com.luohengxu.backend.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {
    
    private final BlogPostRepository blogPostRepository;
    
    public List<BlogPostResponse> getAllPublishedPosts() {
        List<BlogPost> posts = blogPostRepository.findByStatusOrderByPublishedAtDesc(BlogPost.PostStatus.PUBLISHED);
        return posts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Page<BlogPostResponse> getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = blogPostRepository.findByStatusOrderByPublishedAtDesc(BlogPost.PostStatus.PUBLISHED, pageable);
        return posts.map(this::convertToResponse);
    }
    
    public List<BlogPostResponse> getAllPosts() {
        List<BlogPost> posts = blogPostRepository.findAll();
        return posts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Optional<BlogPostResponse> getPostById(Long id) {
        return blogPostRepository.findById(id).map(this::convertToResponse);
    }
    
    public List<BlogPostResponse> getPostsByCategory(String category) {
        List<BlogPost> posts = blogPostRepository.findByCategoryAndStatusOrderByPublishedAtDesc(category, BlogPost.PostStatus.PUBLISHED);
        return posts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<BlogPostResponse> getPostsByTag(String tag) {
        List<BlogPost> posts = blogPostRepository.findByTagAndStatus(tag, BlogPost.PostStatus.PUBLISHED);
        return posts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<BlogPostResponse> searchPosts(String query) {
        List<BlogPost> posts = blogPostRepository.searchByContentAndStatus(query, BlogPost.PostStatus.PUBLISHED);
        return posts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public BlogPostResponse createPost(BlogPostRequest request) {
        BlogPost post = convertToEntity(request);
        if ("PUBLISHED".equals(request.getStatus())) {
            post.setPublishedAt(LocalDateTime.now());
        }
        BlogPost savedPost = blogPostRepository.save(post);
        return convertToResponse(savedPost);
    }
    
    public Optional<BlogPostResponse> updatePost(Long id, BlogPostRequest request) {
        Optional<BlogPost> postOpt = blogPostRepository.findById(id);
        if (postOpt.isPresent()) {
            BlogPost post = postOpt.get();
            updatePostFromRequest(post, request);
            
            // 如果從草稿變為發佈，設置發佈時間
            if ("PUBLISHED".equals(request.getStatus()) && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
            
            BlogPost savedPost = blogPostRepository.save(post);
            return Optional.of(convertToResponse(savedPost));
        }
        return Optional.empty();
    }
    
    public boolean deletePost(Long id) {
        if (blogPostRepository.existsById(id)) {
            blogPostRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public void incrementViewCount(Long id) {
        Optional<BlogPost> postOpt = blogPostRepository.findById(id);
        if (postOpt.isPresent()) {
            BlogPost post = postOpt.get();
            post.setViewCount(post.getViewCount() + 1);
            blogPostRepository.save(post);
        }
    }
    
    public List<String> getCategories() {
        return blogPostRepository.findDistinctCategoriesByStatus(BlogPost.PostStatus.PUBLISHED);
    }
    
    public List<String> getTags() {
        return blogPostRepository.findDistinctTagsByStatus(BlogPost.PostStatus.PUBLISHED);
    }
    
    public Optional<Integer> likePost(Long id) {
        Optional<BlogPost> postOpt = blogPostRepository.findById(id);
        if (postOpt.isPresent()) {
            BlogPost post = postOpt.get();
            post.setLikeCount(post.getLikeCount() + 1);
            blogPostRepository.save(post);
            return Optional.of(post.getLikeCount());
        }
        return Optional.empty();
    }
    
    public Optional<Integer> getLikeCount(Long id) {
        Optional<BlogPost> postOpt = blogPostRepository.findById(id);
        return postOpt.map(BlogPost::getLikeCount);
    }
    
    public Optional<Integer> toggleLike(Long id, boolean currentlyLiked) {
        Optional<BlogPost> postOpt = blogPostRepository.findById(id);
        if (postOpt.isPresent()) {
            BlogPost post = postOpt.get();
            if (currentlyLiked) {
                // 當前已點讚，取消點讚（減少點讚數）
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            } else {
                // 當前未點讚，添加點讚（增加點讚數）
                post.setLikeCount(post.getLikeCount() + 1);
            }
            blogPostRepository.save(post);
            return Optional.of(post.getLikeCount());
        }
        return Optional.empty();
    }
    
    private BlogPost convertToEntity(BlogPostRequest request) {
        BlogPost post = new BlogPost();
        updatePostFromRequest(post, request);
        return post;
    }
    
    private void updatePostFromRequest(BlogPost post, BlogPostRequest request) {
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setCoverImage(request.getCoverImage());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setStatus(BlogPost.PostStatus.valueOf(request.getStatus()));
        post.setAuthor(request.getAuthor());
    }
    
    private BlogPostResponse convertToResponse(BlogPost post) {
        BlogPostResponse response = new BlogPostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setExcerpt(post.getExcerpt());
        response.setCoverImage(post.getCoverImage());
        response.setCategory(post.getCategory());
        response.setTags(post.getTags());
        response.setStatus(post.getStatus().name());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setPublishedAt(post.getPublishedAt());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setAuthor(post.getAuthor());
        return response;
    }
} 