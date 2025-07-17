package com.luohengxu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SocialLinkRequest {
    @NotBlank(message = "平台名稱不能為空")
    private String platform;
    
    @NotBlank(message = "URL不能為空")
    @Pattern(regexp = "^(https?://|mailto:|tel:).*", message = "URL格式不正確")
    private String url;
    
    @NotBlank(message = "圖標不能為空")
    private String icon;

    // Constructors
    public SocialLinkRequest() {}

    public SocialLinkRequest(String platform, String url, String icon) {
        this.platform = platform;
        this.url = url;
        this.icon = icon;
    }

    // Getters and Setters
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
} 