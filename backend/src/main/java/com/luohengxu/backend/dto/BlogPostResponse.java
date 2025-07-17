package com.luohengxu.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogPostResponse {
    
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private String coverImage;
    private String category;
    private List<String> tags;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private Integer viewCount;
    private Integer likeCount;
    private String author;
} 