package com.luohengxu.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class UserProfileRequest {
    @NotBlank(message = "姓名不能為空")
    private String name;
    
    @NotBlank(message = "頭像不能為空")
    private String avatar;
    
    @NotBlank(message = "標題不能為空")
    private String title;
    
    private String bio;
    
    @Email(message = "郵箱格式不正確")
    private String email;
    
    private String location;
    private Set<String> skills;
    private Set<SocialLinkRequest> socialLinks;

    // Constructors
    public UserProfileRequest() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public Set<SocialLinkRequest> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(Set<SocialLinkRequest> socialLinks) {
        this.socialLinks = socialLinks;
    }
} 