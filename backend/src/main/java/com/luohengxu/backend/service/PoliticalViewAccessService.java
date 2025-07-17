package com.luohengxu.backend.service;

import com.luohengxu.backend.dto.PoliticalViewAccessRequest;
import com.luohengxu.backend.entity.PoliticalViewAccess;
import com.luohengxu.backend.repository.PoliticalViewAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PoliticalViewAccessService {

    @Autowired
    private PoliticalViewAccessRepository accessRepository;

    // 获取所有访问权限
    public List<PoliticalViewAccess> getAllAccess() {
        return accessRepository.findAll();
    }

    // 获取活跃的访问权限
    public List<PoliticalViewAccess> getActiveAccess() {
        return accessRepository.findByIsActiveTrueOrderByName();
    }

    // 根据ID获取访问权限
    public Optional<PoliticalViewAccess> getAccessById(Long id) {
        return accessRepository.findById(id);
    }

    // 根据姓名验证访问权限
    public boolean validateAccess(String name) {
        return accessRepository.findByNameAndIsActiveTrue(name).isPresent();
    }

    // 创建访问权限
    public PoliticalViewAccess createAccess(PoliticalViewAccessRequest request) {
        if (accessRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("该姓名已存在");
        }

        PoliticalViewAccess access = new PoliticalViewAccess();
        access.setName(request.getName());
        access.setDescription(request.getDescription());
        access.setIsActive(request.getIsActive());

        return accessRepository.save(access);
    }

    // 更新访问权限
    public PoliticalViewAccess updateAccess(Long id, PoliticalViewAccessRequest request) {
        PoliticalViewAccess access = accessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到访问权限"));

        // 检查姓名是否已被其他记录使用
        if (!access.getName().equals(request.getName()) && 
            accessRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("该姓名已存在");
        }

        access.setName(request.getName());
        access.setDescription(request.getDescription());
        access.setIsActive(request.getIsActive());

        return accessRepository.save(access);
    }

    // 删除访问权限
    public void deleteAccess(Long id) {
        if (!accessRepository.existsById(id)) {
            throw new RuntimeException("找不到访问权限");
        }
        accessRepository.deleteById(id);
    }

    // 切换激活状态
    public PoliticalViewAccess toggleActive(Long id) {
        PoliticalViewAccess access = accessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到访问权限"));

        access.setIsActive(!access.getIsActive());
        return accessRepository.save(access);
    }
} 