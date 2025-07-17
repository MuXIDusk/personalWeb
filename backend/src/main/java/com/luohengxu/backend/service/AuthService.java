package com.luohengxu.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Value("${app.blog.admin.password}")
    private String blogAdminPassword;
    
    @Value("${app.political.password}")
    private String politicalPassword;
    
    public boolean validateBlogAdminPassword(String password) {
        return blogAdminPassword != null && blogAdminPassword.equals(password);
    }
    
    public boolean validatePoliticalPassword(String password) {
        return politicalPassword != null && politicalPassword.equals(password);
    }
} 