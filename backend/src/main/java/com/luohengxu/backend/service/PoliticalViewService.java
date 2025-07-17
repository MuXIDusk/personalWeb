package com.luohengxu.backend.service;

import com.luohengxu.backend.dto.PoliticalViewRequest;
import com.luohengxu.backend.entity.PoliticalView;
import com.luohengxu.backend.repository.PoliticalViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PoliticalViewService {

    private final PoliticalViewRepository politicalViewRepository;

    // 獲取所有政治觀點
    public List<PoliticalView> getAllPoliticalViews() {
        return politicalViewRepository.findAll();
    }

    // 獲取活躍的政治觀點
    public List<PoliticalView> getActivePoliticalViews() {
        return politicalViewRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    // 根據ID獲取政治觀點
    public Optional<PoliticalView> getPoliticalViewById(Long id) {
        return politicalViewRepository.findById(id);
    }

    // 根據類型獲取政治觀點
    public List<PoliticalView> getPoliticalViewsByType(PoliticalView.ViewType type) {
        return politicalViewRepository.findByTypeOrderByDisplayOrderAsc(type);
    }

    // 獲取活躍的核心價值觀
    public List<PoliticalView> getActiveCoreValues() {
        return politicalViewRepository.findActiveCoreValues();
    }

    // 獲取活躍的政治測試結果
    public List<PoliticalView> getActiveTestResults() {
        return politicalViewRepository.findActiveTestResults();
    }

    // 創建政治觀點
    public PoliticalView createPoliticalView(PoliticalViewRequest request) {
        // 檢查標題是否已存在
        if (politicalViewRepository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("標題已存在: " + request.getTitle());
        }

        PoliticalView politicalView = convertToEntity(request);
        return politicalViewRepository.save(politicalView);
    }

    // 更新政治觀點
    public PoliticalView updatePoliticalView(Long id, PoliticalViewRequest request) {
        PoliticalView existingView = politicalViewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到政治觀點，ID: " + id));

        // 檢查標題是否已存在（排除當前記錄）
        if (politicalViewRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new IllegalArgumentException("標題已存在: " + request.getTitle());
        }

        updateEntityFromRequest(existingView, request);
        return politicalViewRepository.save(existingView);
    }

    // 刪除政治觀點
    public void deletePoliticalView(Long id) {
        if (!politicalViewRepository.existsById(id)) {
            throw new RuntimeException("找不到政治觀點，ID: " + id);
        }
        politicalViewRepository.deleteById(id);
    }

    // 切換激活狀態
    public PoliticalView toggleActive(Long id) {
        PoliticalView politicalView = politicalViewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到政治觀點，ID: " + id));
        
        politicalView.setIsActive(!politicalView.getIsActive());
        return politicalViewRepository.save(politicalView);
    }

    // 更新顯示順序
    public PoliticalView updateDisplayOrder(Long id, Integer displayOrder) {
        PoliticalView politicalView = politicalViewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到政治觀點，ID: " + id));
        
        politicalView.setDisplayOrder(displayOrder);
        return politicalViewRepository.save(politicalView);
    }

    // 轉換DTO到實體
    private PoliticalView convertToEntity(PoliticalViewRequest request) {
        PoliticalView politicalView = new PoliticalView();
        updateEntityFromRequest(politicalView, request);
        return politicalView;
    }

    // 更新實體字段
    private void updateEntityFromRequest(PoliticalView politicalView, PoliticalViewRequest request) {
        politicalView.setType(request.getType());
        politicalView.setTitle(request.getTitle());
        politicalView.setDescription(request.getDescription());
        politicalView.setIcon(request.getIcon());
        politicalView.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        politicalView.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        // 政治測試結果特有字段
        if (request.getType() == PoliticalView.ViewType.TEST_RESULT) {
            politicalView.setPosition(request.getPosition());
            politicalView.setDetails(request.getDetails());
            politicalView.setTestDate(request.getTestDate());
        }
    }
} 