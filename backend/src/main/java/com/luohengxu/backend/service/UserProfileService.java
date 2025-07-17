package com.luohengxu.backend.service;

import com.luohengxu.backend.dto.SocialLinkRequest;
import com.luohengxu.backend.dto.UserProfileRequest;
import com.luohengxu.backend.entity.SocialLink;
import com.luohengxu.backend.entity.UserProfile;
import com.luohengxu.backend.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    // 獲取用戶資料 (假設只有一個用戶)
    public Optional<UserProfile> getUserProfile() {
        List<UserProfile> profiles = userProfileRepository.findAll();
        return profiles.isEmpty() ? Optional.empty() : Optional.of(profiles.get(0));
    }

    // 根據ID獲取用戶資料
    public Optional<UserProfile> getUserProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    // 創建用戶資料
    public UserProfile createUserProfile(UserProfileRequest request) {
        UserProfile userProfile = convertToEntity(request);
        return userProfileRepository.save(userProfile);
    }

    // 更新用戶資料
    public UserProfile updateUserProfile(Long id, UserProfileRequest request) {
        UserProfile existingProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到用戶資料，ID: " + id));

        updateEntityFromRequest(existingProfile, request);
        return userProfileRepository.save(existingProfile);
    }

    // 更新主要用戶資料 (如果沒有則創建)
    public UserProfile updateMainUserProfile(UserProfileRequest request) {
        Optional<UserProfile> profileOpt = getUserProfile();
        
        if (profileOpt.isPresent()) {
            UserProfile existingProfile = profileOpt.get();
            updateEntityFromRequest(existingProfile, request);
            return userProfileRepository.save(existingProfile);
        } else {
            return createUserProfile(request);
        }
    }

    // 刪除用戶資料
    public void deleteUserProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new RuntimeException("找不到用戶資料，ID: " + id);
        }
        userProfileRepository.deleteById(id);
    }

    // 檢查郵箱是否已存在
    public boolean emailExists(String email) {
        return userProfileRepository.existsByEmail(email);
    }

    // 根據郵箱查找用戶
    public Optional<UserProfile> getUserByEmail(String email) {
        return userProfileRepository.findByEmail(email);
    }

    // 獲取所有技能
    public List<String> getAllSkills() {
        return userProfileRepository.findAllSkills();
    }

    // 轉換DTO到實體
    private UserProfile convertToEntity(UserProfileRequest request) {
        UserProfile userProfile = new UserProfile();
        updateEntityFromRequest(userProfile, request);
        return userProfile;
    }

    // 從請求更新實體
    private void updateEntityFromRequest(UserProfile userProfile, UserProfileRequest request) {
        userProfile.setName(request.getName());
        userProfile.setAvatar(request.getAvatar());
        userProfile.setTitle(request.getTitle());
        userProfile.setBio(request.getBio());
        userProfile.setEmail(request.getEmail());
        userProfile.setLocation(request.getLocation());
        userProfile.setSkills(request.getSkills());

        // 處理社交連結
        if (request.getSocialLinks() != null) {
            // 清除現有的社交連結
            if (userProfile.getSocialLinks() != null) {
                userProfile.getSocialLinks().clear();
            }

            // 添加新的社交連結
            Set<SocialLink> socialLinks = request.getSocialLinks().stream()
                    .map(linkRequest -> new SocialLink(
                            linkRequest.getPlatform(),
                            linkRequest.getUrl(),
                            linkRequest.getIcon(),
                            userProfile
                    ))
                    .collect(Collectors.toSet());
            
            userProfile.setSocialLinks(socialLinks);
        }
    }
} 