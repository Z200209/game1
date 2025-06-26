package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.app.annotations.VerifiedUser;
import com.example.app.domain.*;
import com.example.app.domain.game.GameInfoVO;
import com.example.app.domain.game.GameListVO;
import com.example.app.domain.game.GameVO;
import com.example.app.domain.game.ImageVO;
import com.example.module.entity.*;
import com.example.module.service.Game.GameService;
import com.example.module.service.Game.TypeService;
import com.example.module.service.TagService;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 游戏控制器
 */
@Slf4j
@RestController
@RequestMapping("/app/game")
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    /**
     * 获取游戏详情
     */
    @RequestMapping("/info")
    public Response gameInfo(@VerifiedUser User loginUser,
                                        @RequestParam(name = "gameId") BigInteger gameId) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("用户未登录");
            return new Response(1002);
        }
        
        log.info("用户 {} 请求游戏详情，gameId: {}", loginUser.getId(), gameId);
        
        // 获取游戏信息
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (Exception e) {
            log.error("获取游戏信息失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (game == null) {
            log.info("未找到游戏信息：{}", gameId);
            return new Response(4004);
        }
        
        // 获取类型信息
        Type type = null;
        String typeName = null;
        String typeImage = null;
        
        if (game.getTypeId() != null) {
            try {
                type = typeService.getById(game.getTypeId());
                if (type != null) {
                    typeName = type.getTypeName();
                    typeImage = type.getImage();
                }
            } catch (Exception e) {
                log.error("获取游戏类型失败: {}", e.getMessage(), e);
                // 继续处理，类型不是必须的
            }
        }
        List<Tag> tag = tagService.getTagsByGameId(gameId);
        List<String> tagNames = tag.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        
        // 构建返回对象
        GameInfoVO gameInfo = new GameInfoVO()
                .setGameId(game.getId())
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher())
                .setTags(tagNames);
                
        // 将图片字符串按 "$" 拆分为列表
        if (game.getImages() != null && !game.getImages().isEmpty()) {
            gameInfo.setImages(Arrays.asList(game.getImages().split("\\$")));
        }
        try {
            List<BaseIntroductionVO> introductionList =JSON.parseArray(game.getGameIntroduction(), BaseIntroductionVO.class);
            gameInfo.setGameIntroduction(introductionList);
        }
        catch (Exception e) {
            log.error("解析游戏介绍失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        return new Response(1001, gameInfo);
    }

    /**
     * 获取游戏列表
     */
    @RequestMapping("/list")
    public Response gameList(@VerifiedUser User loginUser,
                                        @RequestParam(name = "keyword", required=false) String keyword,
                                        @RequestParam(name = "typeId", required=false) BigInteger typeId,
                                        @RequestParam(name = "wp", required=false) String wp) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("用户未登录");
            return new Response<>(1002);
        }
        
        log.info("用户 {} 请求游戏列表，keyword: {}, typeId: {}", loginUser.getId(), keyword, typeId);

        int currentPageSize = 10;
        Integer currentPage;
        
        // 解析wp参数
        if (wp != null && !wp.isEmpty()) {
            try {
                byte[] bytes = Base64.getUrlDecoder().decode(wp);
                String json = new String(bytes, StandardCharsets.UTF_8);
                Wp receiveWp = JSON.parseObject(json, Wp.class);
                currentPage = receiveWp.getPage();
                
                if (currentPage == 1) {
                    return new Response<>(4005);
                }
                
                currentPageSize = receiveWp.getPageSize();
                keyword = receiveWp.getKeyword();
                typeId = receiveWp.getTypeId();
            } catch (Exception e) {
                log.error("解析wp参数失败: {}", e.getMessage(), e);
                return new Response(4004);
            }
        } else {
            currentPage = 1;
        }
        
        // 获取游戏列表
        List<Game> gameList;
        try {
            gameList = gameService.getAllGame(currentPage, currentPageSize, keyword, typeId);
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        // 收集类型ID
        Set<BigInteger> typeIdSet = new HashSet<>();
        for (Game game : gameList) {
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
                // 继续处理，类型不是必须的
            }
        }
        
        // 构建输出的wp对象
        Wp outputWp = new Wp();
        outputWp.setKeyword(keyword)
                .setTypeId(typeId)
                .setPage(currentPage + 1)
                .setPageSize(currentPageSize);
        
        // 编码wp
        String encodeWp;
        try {
            encodeWp = Base64.getUrlEncoder().encodeToString(JSON.toJSONString(outputWp).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("编码wp失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        // 构建游戏列表数据
        List<GameVO> gameVOList = new ArrayList<>();
        for (Game game : gameList) {
            if (game.getTypeId() == null || game.getImages() == null || game.getImages().isEmpty()) {
                log.info("游戏数据不完整，跳过：{}", game.getId());
                continue;
            }
            
            String typeName = typeMap.get(game.getTypeId());
            if (typeName == null) {
                log.info("未找到游戏类型名称：{}", game.getTypeId());
                continue;
            }
            
            String image = game.getImages().split("\\$")[0];
            
            // 计算图片宽高比
            float ar = 0;
            try {
                String regex = ".*_(\\d+)x(\\d+)\\.png";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(image);
                if (matcher.find()) {
                    int width = Integer.parseInt(matcher.group(1));
                    int height = Integer.parseInt(matcher.group(2));
                    ar = (float) width / height;
                }
            } catch (Exception e) {
                log.info("解析图片尺寸失败: {}", e.getMessage());
                // 继续处理，宽高比不是必须的
            }
            
            ImageVO imageVO = new ImageVO()
                    .setSrc(image)
                    .setAr(ar);
            
            GameVO gameVO = new GameVO()
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setTypeName(typeName)
                    .setImage(imageVO);
            
            gameVOList.add(gameVO);
        }
        
        // 构建最终响应对象
        GameListVO result = new GameListVO()
                .setGameList(gameVOList)
                .setWp(encodeWp);
        
        return new Response(1001, result);
    }
}




