package com.example.module.service;

import com.example.module.annotations.DataSource;
import com.example.module.config.data.DataSourceType;
import com.example.module.entity.*;
import com.example.module.service.Game.GameService;
import com.example.module.service.Game.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 首页服务类 - 使用多线程优化性能
 */
@Slf4j
@Service
public class HomeService {
    
    @Autowired
    private TypeService typeService;
    
    @Autowired
    private GameService gameService;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    /**
     * 获取首页所有数据 - 多线程并发获取
     */
    @DataSource(DataSourceType.SLAVE)
    public Map<String, Object> getHomePageData() {
        log.info("开始获取首页数据，使用多线程并发处理");
        
        try {
            // 使用CompletableFuture并发执行四个任务
            CompletableFuture<List<Banner>> bannerFuture = CompletableFuture
                .supplyAsync(this::getBannerList, executorService);
            
            CompletableFuture<List<Type>> channelFuture = CompletableFuture
                .supplyAsync(this::getChannelList, executorService);
            
            CompletableFuture<List<Event>> eventFuture = CompletableFuture
                .supplyAsync(this::getEventList, executorService);
            
            CompletableFuture<List<Game>> productFuture = CompletableFuture
                .supplyAsync(this::getRecommendProductList, executorService);
            
            // 等待所有任务完成并获取结果
            List<Banner> banners = bannerFuture.get();
            List<Type> channels = channelFuture.get();
            List<Event> events = eventFuture.get();
            List<Game> products = productFuture.get();
            
            // 组装返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("banners", banners);
            result.put("channels", channels);
            result.put("events", events);
            result.put("recommendProducts", products);
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("首页数据获取完成，banners: {}, channels: {}, events: {}, products: {}", 
                    banners.size(), channels.size(), events.size(), products.size());
            
            return result;
            
        } catch (Exception e) {
            log.error("获取首页数据失败", e);
            throw new RuntimeException("获取首页数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取Banner列表 - 模拟数据
     */
    private List<Banner> getBannerList() {
        log.info("开始获取Banner数据");
        
        // 模拟网络延迟
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(BigInteger.valueOf(1), 
            "https://example.com/banner1.jpg", 
            "/game/detail/1", 
            "热门游戏推荐", 1, 1));
        banners.add(new Banner(BigInteger.valueOf(2), 
            "https://example.com/banner2.jpg", 
            "/activity/summer", 
            "夏日活动", 2, 1));
        banners.add(new Banner(BigInteger.valueOf(3), 
            "https://example.com/banner3.jpg", 
            "/game/new", 
            "新游上线", 3, 1));
        
        log.info("Banner数据获取完成，数量: {}", banners.size());
        return banners;
    }
    
    /**
     * 获取频道列表 - 使用Type数据
     */
    private List<Type> getChannelList() {
        log.info("开始获取Channel数据");
        
        try {
            // 获取启用的分类作为频道
            List<Type> types = typeService.getAllType(null);
            
            // 限制返回数量，只返回前8个
            List<Type> channels = types.size() > 8 ? 
                types.subList(0, 8) : types;
            
            log.info("Channel数据获取完成，数量: {}", channels.size());
            return channels;
            
        } catch (Exception e) {
            log.error("获取Channel数据失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取活动列表 - 模拟数据
     */
    private List<Event> getEventList() {
        log.info("开始获取Event数据");
        
        // 模拟网络延迟
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<Event> events = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        events.add(new Event(BigInteger.valueOf(1), 
            "夏日狂欢节", 
            "https://example.com/event1.jpg", 
            "夏日狂欢节火热进行中，参与即可获得丰厚奖励！", 
            "/event/summer", 
            1, 1, now, now.plusDays(7)));
        
        events.add(new Event(BigInteger.valueOf(2), 
            "新用户专享", 
            "https://example.com/event2.jpg", 
            "新用户注册即送豪华大礼包，限时优惠不容错过！", 
            "/event/newuser", 
            2, 1, now, now.plusDays(30)));
        
        events.add(new Event(BigInteger.valueOf(3), 
            "周末双倍经验", 
            "https://example.com/event3.jpg", 
            "周末游戏双倍经验，快来体验吧！", 
            "/event/weekend", 
            3, 1, now, now.plusDays(2)));
        
        log.info("Event数据获取完成，数量: {}", events.size());
        return events;
    }
    
    /**
     * 获取推荐商品列表 - 使用Game数据
     */
    private List<Game> getRecommendProductList() {
        log.info("开始获取推荐商品数据");
        
        try {
            // 获取游戏数据作为推荐商品
            List<Game> games = gameService.getAllGame(1, 12, null, null);
            
            log.info("推荐商品数据获取完成，数量: {}", games.size());
            return games;
            
        } catch (Exception e) {
            log.error("获取推荐商品数据失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取单个模块数据 - 用于单独刷新某个模块
     */
    @DataSource(DataSourceType.SLAVE)
    public Object getModuleData(String moduleType) {
        log.info("获取模块数据: {}", moduleType);
        
        switch (moduleType.toLowerCase()) {
            case "banner":
                return getBannerList();
            case "channel":
                return getChannelList();
            case "event":
                return getEventList();
            case "product":
                return getRecommendProductList();
            default:
                throw new IllegalArgumentException("不支持的模块类型: " + moduleType);
        }
    }
}