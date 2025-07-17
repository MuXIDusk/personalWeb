package com.luohengxu.backend.repository;

import com.luohengxu.backend.entity.PoliticalView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliticalViewRepository extends JpaRepository<PoliticalView, Long> {

    // 根據類型查詢
    List<PoliticalView> findByTypeOrderByDisplayOrderAsc(PoliticalView.ViewType type);

    // 查詢活躍的記錄
    List<PoliticalView> findByIsActiveTrueOrderByDisplayOrderAsc();

    // 根據類型查詢活躍的記錄
    List<PoliticalView> findByTypeAndIsActiveTrueOrderByDisplayOrderAsc(PoliticalView.ViewType type);

    // 獲取核心價值觀
    @Query("SELECT p FROM PoliticalView p WHERE p.type = 'CORE_VALUE' AND p.isActive = true ORDER BY p.displayOrder ASC")
    List<PoliticalView> findActiveCoreValues();

    // 獲取政治測試結果
    @Query("SELECT p FROM PoliticalView p WHERE p.type = 'TEST_RESULT' AND p.isActive = true ORDER BY p.displayOrder ASC")
    List<PoliticalView> findActiveTestResults();

    // 檢查標題是否已存在
    boolean existsByTitleAndIdNot(String title, Long id);
    
    boolean existsByTitle(String title);
} 