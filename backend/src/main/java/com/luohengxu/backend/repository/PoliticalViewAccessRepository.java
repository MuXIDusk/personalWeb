package com.luohengxu.backend.repository;

import com.luohengxu.backend.entity.PoliticalViewAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoliticalViewAccessRepository extends JpaRepository<PoliticalViewAccess, Long> {
    
    List<PoliticalViewAccess> findByIsActiveTrueOrderByName();
    
    Optional<PoliticalViewAccess> findByNameAndIsActiveTrue(String name);
    
    boolean existsByName(String name);
} 