package com.luohengxu.backend.repository;

import com.luohengxu.backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    // 根據郵箱查找
    Optional<UserProfile> findByEmail(String email);
    
    // 根據姓名查找
    Optional<UserProfile> findByName(String name);
    
    // 檢查郵箱是否存在
    boolean existsByEmail(String email);
    
    // 獲取所有技能
    @Query("SELECT DISTINCT s FROM UserProfile u JOIN u.skills s ORDER BY s")
    List<String> findAllSkills();
} 