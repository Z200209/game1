package com.example.console.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.console.domain.BaseIntroductionVO;
import com.example.module.entity.Tag;
import com.example.module.service.TagService;
import com.example.module.utils.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.example.console.annotations.VerifiedUser;
import com.example.console.domain.game.DetailVO;
import com.example.console.domain.game.GameListVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.User;
import com.example.module.service.Game.GameService;
import com.example.module.service.Game.TypeService;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * 游戏控制器
 */
@Slf4j
@RestController("consoleGameController")
@RequestMapping("/console/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    /**
     * 创建游戏
     */
    @PostMapping("/create")
    public Response createGame(
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images,
            @RequestParam(name = "tags") String tags) {

        // 验证用户是否登录

        // 参数验证
        gameName = gameName.trim();
        gamePublisher = gamePublisher.trim();
        tags = tags.trim();

        if (gameName.isEmpty()) {
            log.info("游戏名称不能为空字符串");
            return new Response(4005);
        }

        if (price < 0) {
            log.info("游戏价格不能为负数");
            return new Response(4005);
        }

        if (gameIntroduction == null) {
            log.info("游戏介绍不能为空字符串");
            return new Response(4005);
        }
        if(tags.isEmpty()){
            log.info("游戏标签不能为空字符串");
            return new Response(4005);
        }

        // 创建游戏
        BigInteger gameId;
        try {
            gameId = gameService.edit(null, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId, tags);
        } catch (Exception e) {
            log.error("创建游戏失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        return new Response<>(1001, "创建成功，ID: " + gameId);
    }

    /**
     * 更新游戏信息
     */
    @RequestMapping("/update")
    public Response updateGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId", required = false) BigInteger gameId,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName", required = false) String gameName,
            @RequestParam(name = "price", required = false) Float price,
            @RequestParam(name = "gameIntroduction", required = false) String gameIntroduction,
            @RequestParam(name = "gameDate", required = false) String gameDate,
            @RequestParam(name = "gamePublisher", required = false) String gamePublisher,
            @RequestParam(name = "images", required = false) String images,
            @RequestParam(name = "tags", required = false) String tags) {

        // 验证用户是否登录
//        if (loginUser == null) {
//            log.warn("未登录用户尝试更新游戏");
//            return new Response(1002);
//        }

        // 参数验证
        if (gameId == null) {
            log.info("游戏ID不能为空");
            return new Response(4005);
        }

        if (gameName != null) {
            gameName = gameName.trim();
            if (gameName.isEmpty()) {
                log.info("游戏名称不能为空字符串");
                return new Response(4005);
            }
        }

        if (gamePublisher != null) {
            gamePublisher = gamePublisher.trim();
        }

        if (tags != null) {
            tags = tags.trim();
        }

        if (price != null && price < 0) {
            log.info("游戏价格不能为负数");
            return new Response(4005);
        }

        if (gameIntroduction != null && gameIntroduction.trim().isEmpty()) {
            log.info("游戏介绍不能为空字符串");
            return new Response(4005);
        }
        // 检查游戏是否存在
        Game existingGame;
        try {
            existingGame = gameService.getById(gameId);
        } catch (Exception e) {
            log.error("获取游戏信息失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        if (existingGame == null) {
            log.info("未找到游戏: {}", gameId);
            return new Response(4006);
        }
        
        if (tags != null && tags.isEmpty()) {
            log.info("游戏标签不能为空字符串");
            return new Response(4005);
        }

        // 使用现有游戏信息作为默认值，只更新提供的参数
        String finalGameName = gameName != null ? gameName : existingGame.getGameName();
        Float finalPrice = price != null ? price : existingGame.getPrice();
        String finalGameIntroduction = gameIntroduction != null ? gameIntroduction : existingGame.getGameIntroduction();
        String finalGameDate = gameDate != null ? gameDate : existingGame.getGameDate();
        String finalGamePublisher = gamePublisher != null ? gamePublisher : existingGame.getGamePublisher();
        String finalImages = images != null ? images : existingGame.getImages();
        BigInteger finalTypeId = typeId != null ? typeId : existingGame.getTypeId();
        
        // 对于tags，如果没有提供则保持原有标签不变
        String finalTags = tags;
        if (tags == null) {
            // 获取现有标签
            List<Tag> existingTags = tagService.getTagsByGameId(gameId);
            if (!existingTags.isEmpty()) {
                finalTags = existingTags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(","));
            }
        }

        // 更新游戏
        try {
            gameService.edit(gameId, finalGameName, finalPrice, finalGameIntroduction, finalGameDate, finalGamePublisher, finalImages, finalTypeId, finalTags);
        } catch (Exception e) {
            log.error("更新游戏失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        return new Response(1001);
    }

    /**
     * 获取游戏列表
     */
    @RequestMapping("/list")
    public Response gameList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId) {

        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取游戏列表");
            return new Response(1002);
        }

        int pageSize = 10;

        // 获取游戏列表和总数
        List<Game> gameList;
        Integer total;
        try {
            gameList = gameService.getAllGame(page, pageSize, keyword, typeId);
            total = gameService.getTotalCount(keyword);
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        // 收集类型ID和游戏ID
        Set<BigInteger> typeIdSet = new HashSet<>();
        Set<BigInteger> gameIdSet = new HashSet<>();
        for (Game game : gameList) {
            gameIdSet.add(game.getId());
            BigInteger tid = game.getTypeId();
            if (tid != null) {
                typeIdSet.add(tid);
            }
        }

        // 获取类型信息
        Map<BigInteger, String> typeMap = new HashMap<>();
        if (!typeIdSet.isEmpty()) {
            try {
                List<Type> types = typeService.getTypeByIds(typeIdSet);
                for (Type type : types) {
                    typeMap.put(type.getId(), type.getTypeName());
                }
            } catch (Exception e) {
                log.error("获取类型信息失败: {}", e.getMessage(), e);
            }
        }

        // 构建游戏列表数据
        List<GameListVO> gameVOList = new ArrayList<>();
        for (Game game : gameList) {
                String typeName = typeMap.get(game.getTypeId());
                String formattedCreateTime = BaseUtils.timeStamp2DateGMT(game.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                String formattedUpdateTime = BaseUtils.timeStamp2DateGMT(game.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");

                GameListVO gameVO = new GameListVO()
                        .setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setTypeName(typeName)
                        .setPrice(game.getPrice())
                        .setCreateTime(formattedCreateTime)
                        .setUpdateTime(formattedUpdateTime);

                gameVOList.add(gameVO);
        }

        // 构建最终响应对象
        Map<String, Object> result = new HashMap<>();
        result.put("list", gameVOList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return new Response(1001, result);
    }



    /**
     * 获取游戏详情
     */
    @RequestMapping("/info")
    public Response gameInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId) {

        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取游戏详情");
            return new Response(1002);
        }

        // 获取游戏信息
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (Exception e) {
            log.error("获取游戏详情失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        if (game == null) {
            log.info("未找到游戏信息：{}", gameId);
            return new Response(4006);
        }

        // 格式化时间
        String formattedCreateTime;
        String formattedUpdateTime;
        try {
            formattedCreateTime = BaseUtils.timeStamp2DateGMT(game.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            formattedUpdateTime = BaseUtils.timeStamp2DateGMT(game.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            log.error("格式化时间失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        // 获取类型信息
        BigInteger typeId = game.getTypeId();
        Type type;
        String typeName;
        String typeImage;
        try {
            type = typeService.getById(typeId);
            if (type == null) {
                log.info("未找到游戏类型：{}", typeId);
                return new Response(4006);
            }
            typeName = type.getTypeName();
            typeImage = type.getImage();
        } catch (Exception e) {
            log.error("获取游戏类型失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        // 获取标签信息
        List<Tag> tag = tagService.getTagsByGameId(gameId);
        List<String> tagNames = tag.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        // 构建响应对象
        DetailVO detailVO = new DetailVO()
                .setGameId(game.getId())
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher())
                .setCreateTime(formattedCreateTime)
                .setUpdateTime(formattedUpdateTime)
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setTags(tagNames);
        
        // 处理图片列表
        if (game.getImages() != null && !game.getImages().isEmpty()) {
            detailVO.setImages(Arrays.asList(game.getImages().split("\\$")));
        }

        try {
            List<BaseIntroductionVO> introductionList = JSON.parseArray(game.getGameIntroduction(), BaseIntroductionVO.class);
            detailVO.setGameIntroduction(introductionList);
        }
        catch (Exception e) {
            log.error("解析游戏介绍失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        return new Response(1001, detailVO);
    }

    /**
     * 删除游戏
     */
    @RequestMapping("/delete")
    public Response deleteGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试删除游戏");
            return new Response(1002);
        }
        
        try {
            // 检查游戏是否存在
            Game game = gameService.getById(gameId);
            if (game == null) {
                log.info("未找到游戏: {}", gameId);
                return new Response<>(4004);
            }
            
            // 删除游戏
            int result = gameService.delete(gameId);
            if (result == 1) {
                return new Response(1001);
            } else {
                return new Response(4004);
            }
        } catch (Exception e) {
            log.error("删除游戏失败", e);
            return new Response(4004);
        }
    }
}













