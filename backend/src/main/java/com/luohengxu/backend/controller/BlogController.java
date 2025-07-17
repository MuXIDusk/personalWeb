package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.BlogPostRequest;
import com.luohengxu.backend.dto.BlogPostResponse;
import com.luohengxu.backend.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}) // 允許前端跨域訪問
public class BlogController {
    
    private final BlogService blogService;
    
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<BlogPostResponse>>> getPublishedPosts() {
        List<BlogPostResponse> posts = blogService.getAllPublishedPosts();
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @GetMapping("/posts/page")
    public ResponseEntity<ApiResponse<Page<BlogPostResponse>>> getPublishedPostsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogPostResponse> posts = blogService.getPublishedPosts(page, size);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @GetMapping("/posts/all")
    public ResponseEntity<ApiResponse<List<BlogPostResponse>>> getAllPosts() {
        List<BlogPostResponse> posts = blogService.getAllPosts();
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getPostById(@PathVariable Long id) {
        Optional<BlogPostResponse> post = blogService.getPostById(id);
        if (post.isPresent()) {
            // 增加瀏覽次數
            blogService.incrementViewCount(id);
            return ResponseEntity.ok(ApiResponse.success(post.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/posts/category/{category}")
    public ResponseEntity<ApiResponse<List<BlogPostResponse>>> getPostsByCategory(@PathVariable String category) {
        List<BlogPostResponse> posts = blogService.getPostsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @GetMapping("/posts/tag/{tag}")
    public ResponseEntity<ApiResponse<List<BlogPostResponse>>> getPostsByTag(@PathVariable String tag) {
        List<BlogPostResponse> posts = blogService.getPostsByTag(tag);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<List<BlogPostResponse>>> searchPosts(@RequestParam String q) {
        List<BlogPostResponse> posts = blogService.searchPosts(q);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<BlogPostResponse>> createPost(@Valid @RequestBody BlogPostRequest request) {
        BlogPostResponse post = blogService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("文章創建成功", post));
    }
    
    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> updatePost(
            @PathVariable Long id, 
            @Valid @RequestBody BlogPostRequest request) {
        Optional<BlogPostResponse> updatedPost = blogService.updatePost(id, request);
        if (updatedPost.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("文章更新成功", updatedPost.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        boolean deleted = blogService.deletePost(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("文章刪除成功", null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = blogService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<String>>> getTags() {
        List<String> tags = blogService.getTags();
        return ResponseEntity.ok(ApiResponse.success(tags));
    }
    
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponse<Integer>> likePost(@PathVariable Long id) {
        Optional<Integer> likeCount = blogService.likePost(id);
        if (likeCount.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("點讚成功", likeCount.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/posts/{id}/toggle-like")
    public ResponseEntity<ApiResponse<Integer>> toggleLike(@PathVariable Long id, @RequestParam boolean currentlyLiked) {
        Optional<Integer> likeCount = blogService.toggleLike(id, currentlyLiked);
        if (likeCount.isPresent()) {
            String message = currentlyLiked ? "取消點讚成功" : "點讚成功";
            return ResponseEntity.ok(ApiResponse.success(message, likeCount.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/posts/{id}/likes")
    public ResponseEntity<ApiResponse<Integer>> getLikeCount(@PathVariable Long id) {
        Optional<Integer> likeCount = blogService.getLikeCount(id);
        if (likeCount.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(likeCount.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 