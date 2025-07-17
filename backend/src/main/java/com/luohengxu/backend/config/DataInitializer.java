package com.luohengxu.backend.config;

import com.luohengxu.backend.entity.*;
import com.luohengxu.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private MediaItemRepository mediaItemRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PoliticalViewRepository politicalViewRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeBlogData();
        initializeMediaData();
        initializeUserProfileData();
        initializePoliticalViewData();
        System.out.println("所有示例數據初始化完成");
    }

    private void initializeBlogData() {
        if (blogPostRepository.count() > 0) {
            return;
        }

        // 創建示例博客文章
        BlogPost post1 = new BlogPost();
        post1.setTitle("歡迎來到我的個人網站");
        post1.setContent("這是我的第一篇博客文章，歡迎大家來到我的個人網站。在這裡我會分享我的學習心得、技術見解以及生活感悟。希望能和大家一起交流學習，共同進步。");
        post1.setExcerpt("歡迎來到我的個人網站，這裡將分享我的思考和創作。");
        post1.setCategory("生活");
        post1.setTags(Arrays.asList("歡迎", "第一篇", "個人網站"));
        post1.setStatus(BlogPost.PostStatus.PUBLISHED);
        post1.setAuthor("站長");
        post1.setViewCount(42);
        post1.setPublishedAt(LocalDateTime.now().minusDays(5));

        BlogPost post2 = new BlogPost();
        post2.setTitle("關於這個網站的技術架構");
        post2.setContent("這個網站使用了現代化的技術棧進行開發。前端採用Vue 3 + TypeScript + Vite，後端使用Spring Boot + JPA + H2數據庫。整個項目採用前後端分離的架構，API設計遵循RESTful規範。");
        post2.setExcerpt("分享一下這個個人網站的技術選型和架構設計思路。");
        post2.setCategory("技術");
        post2.setTags(Arrays.asList("Vue3", "Spring Boot", "架構設計"));
        post2.setStatus(BlogPost.PostStatus.PUBLISHED);
        post2.setAuthor("站長");
        post2.setViewCount(68);
        post2.setPublishedAt(LocalDateTime.now().minusDays(3));

        blogPostRepository.saveAll(Arrays.asList(post1, post2));
    }

    private void initializeMediaData() {
        if (mediaItemRepository.count() > 0) {
            return;
        }

        // 創建示例媒體項目
        MediaItem book1 = new MediaItem();
        book1.setTitle("深入理解計算機系統");
        book1.setOriginalTitle("Computer Systems: A Programmer's Perspective");
        book1.setCover("https://via.placeholder.com/300x400?text=CSAPP");
        book1.setType(MediaItem.MediaType.BOOK);
        book1.setCreator("Randal E. Bryant, David R. O'Hallaron");
        book1.setGenre(new HashSet<>(Arrays.asList("計算機科學", "系統編程")));
        book1.setRating(4.8);
        book1.setStatus(MediaItem.MediaStatus.COMPLETED);
        book1.setDateAdded(LocalDate.now().minusDays(30));
        book1.setDateWatched(LocalDate.now().minusDays(7));
        book1.setReview("一本深入淺出的計算機系統教材，對理解計算機底層原理非常有幫助。");
        book1.setTags(new HashSet<>(Arrays.asList("推薦", "經典", "技術")));

        MediaItem movie1 = new MediaItem();
        movie1.setTitle("星際穿越");
        movie1.setOriginalTitle("Interstellar");
        movie1.setCover("https://via.placeholder.com/300x400?text=Interstellar");
        movie1.setType(MediaItem.MediaType.MOVIE);
        movie1.setCreator("克里斯托弗·諾蘭");
        movie1.setGenre(new HashSet<>(Arrays.asList("科幻", "劇情")));
        movie1.setRating(4.5);
        movie1.setStatus(MediaItem.MediaStatus.COMPLETED);
        movie1.setDateAdded(LocalDate.now().minusDays(15));
        movie1.setDateWatched(LocalDate.now().minusDays(10));
        movie1.setReview("視覺效果震撼，科學設定嚴謹，是一部優秀的科幻電影。");
        movie1.setTags(new HashSet<>(Arrays.asList("科幻", "IMAX", "推薦")));
        movie1.setImdbUrl("https://www.imdb.com/title/tt0816692/");

        MediaItem anime1 = new MediaItem();
        anime1.setTitle("你的名字");
        anime1.setOriginalTitle("君の名は");
        anime1.setCover("https://via.placeholder.com/300x400?text=Your+Name");
        anime1.setType(MediaItem.MediaType.ANIME);
        anime1.setCreator("新海誠");
        anime1.setGenre(new HashSet<>(Arrays.asList("愛情", "奇幻", "動畫")));
        anime1.setRating(4.7);
        anime1.setStatus(MediaItem.MediaStatus.COMPLETED);
        anime1.setDateAdded(LocalDate.now().minusDays(20));
        anime1.setDateWatched(LocalDate.now().minusDays(18));
        anime1.setReview("畫面精美，故事感人，是新海誠的代表作之一。");
        anime1.setTags(new HashSet<>(Arrays.asList("新海誠", "治癒", "推薦")));

        MediaItem tv1 = new MediaItem();
        tv1.setTitle("黑鏡");
        tv1.setOriginalTitle("Black Mirror");
        tv1.setCover("https://via.placeholder.com/300x400?text=Black+Mirror");
        tv1.setType(MediaItem.MediaType.TV);
        tv1.setCreator("查理·布魯克");
        tv1.setGenre(new HashSet<>(Arrays.asList("科幻", "懸疑", "反烏托邦")));
        tv1.setRating(4.3);
        tv1.setStatus(MediaItem.MediaStatus.WATCHING);
        tv1.setDateAdded(LocalDate.now().minusDays(5));
        tv1.setReview("每一集都是獨立的故事，對科技與人性的思考非常深刻。");
        tv1.setTags(new HashSet<>(Arrays.asList("科技", "反思", "英劇")));

        mediaItemRepository.saveAll(Arrays.asList(book1, movie1, anime1, tv1));
    }

    private void initializeUserProfileData() {
        if (userProfileRepository.count() > 0) {
            return;
        }

        // 創建用戶資料
        UserProfile profile = new UserProfile();
        profile.setName("羅恆旭");
        profile.setAvatar("/src/assets/logo.svg");
        profile.setTitle("研究生 & 半個程序員");
        profile.setBio("歡迎來到我的個人網站！我是一名對技術充滿熱情的研究生，專注於深度學習和軟件開發。在這裡我會分享我的學習心得、技術探索和生活感悟。");
        profile.setEmail("luohengxu@163.com");
        profile.setLocation("北京");
        profile.setSkills(new HashSet<>(Arrays.asList("深度學習", "後端開發", "客戶端開發", "Vue.js", "Spring Boot", "Python", "Java")));

        userProfileRepository.save(profile);

        // 創建社交連結
        SocialLink github = new SocialLink();
        github.setPlatform("GitHub");
        github.setUrl("https://github.com/luohengxu");
        github.setIcon("github");
        github.setUserProfile(profile);

        SocialLink email = new SocialLink();
        email.setPlatform("Email");
        email.setUrl("mailto:luohengxu@163.com");
        email.setIcon("email");
        email.setUserProfile(profile);

        Set<SocialLink> socialLinks = new HashSet<>(Arrays.asList(github, email));
        profile.setSocialLinks(socialLinks);

        userProfileRepository.save(profile);
    }

    private void initializePoliticalViewData() {
        if (politicalViewRepository.count() > 0) {
            return;
        }

        // 創建核心價值觀
        PoliticalView equality = new PoliticalView();
        equality.setType(PoliticalView.ViewType.CORE_VALUE);
        equality.setTitle("平等與包容");
        equality.setDescription("支持所有人的基本權利和尊嚴，反對任何形式的歧視");
        equality.setIcon("Heart");
        equality.setDisplayOrder(1);
        equality.setIsActive(true);

        PoliticalView democracy = new PoliticalView();
        democracy.setType(PoliticalView.ViewType.CORE_VALUE);
        democracy.setTitle("民主與法治");
        democracy.setDescription("相信民主制度和法治的重要性，支持透明的治理");
        democracy.setIcon("Shield");
        democracy.setDisplayOrder(2);
        democracy.setIsActive(true);

        PoliticalView responsibility = new PoliticalView();
        responsibility.setType(PoliticalView.ViewType.CORE_VALUE);
        responsibility.setTitle("社會責任");
        responsibility.setDescription("關注社會公正，支持弱勢群體，推動可持續發展");
        responsibility.setIcon("Users");
        responsibility.setDisplayOrder(3);
        responsibility.setIsActive(true);

        // 創建政治測試結果
        PoliticalView testResult1 = new PoliticalView();
        testResult1.setType(PoliticalView.ViewType.TEST_RESULT);
        testResult1.setTitle("政治坐標測試");
        testResult1.setPosition("中左傾向");
        testResult1.setDetails("經濟：偏左 (-2.5)，社會：自由主義 (-3.1)");
        testResult1.setTestDate("2024年1月");
        testResult1.setDisplayOrder(1);
        testResult1.setIsActive(true);

        PoliticalView testResult2 = new PoliticalView();
        testResult2.setType(PoliticalView.ViewType.TEST_RESULT);
        testResult2.setTitle("8Values測試");
        testResult2.setPosition("社會自由主義");
        testResult2.setDetails("經濟軸：平等 65%，外交軸：國際主義 72%，政府軸：自由 68%，社會軸：進步 78%");
        testResult2.setTestDate("2024年2月");
        testResult2.setDisplayOrder(2);
        testResult2.setIsActive(true);

        politicalViewRepository.saveAll(Arrays.asList(
                equality, democracy, responsibility, testResult1, testResult2
        ));
    }
} 