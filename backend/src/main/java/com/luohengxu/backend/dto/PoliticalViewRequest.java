package com.luohengxu.backend.dto;

import com.luohengxu.backend.entity.PoliticalView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PoliticalViewRequest {

    @NotNull(message = "類型不能為空")
    private PoliticalView.ViewType type;

    @NotBlank(message = "標題不能為空")
    private String title;

    private String description;

    private String icon;

    private Integer displayOrder = 0;

    private Boolean isActive = true;

    // 政治測試結果特有字段
    private String position;
    
    private String details;
    
    private String testDate;
} 