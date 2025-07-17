package com.luohengxu.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "social_links")
public class SocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "平台名稱不能為空")
    @Column(nullable = false)
    private String platform;

    @NotBlank(message = "URL不能為空")
    @Pattern(regexp = "^(https?://|mailto:|tel:).*", message = "URL格式不正確")
    @Column(nullable = false)
    private String url;

    @NotBlank(message = "圖標不能為空")
    @Column(nullable = false)
    private String icon;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    // Constructors
    public SocialLink() {}

    public SocialLink(String platform, String url, String icon, UserProfile userProfile) {
        this.platform = platform;
        this.url = url;
        this.icon = icon;
        this.userProfile = userProfile;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
} 