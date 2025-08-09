package com.example.module.mq.consumer;

import com.example.module.config.RabbitMQConfig;
import com.example.module.mq.message.CategoryDeleteMessage;
import com.example.module.service.Game.GameService;
import com.example.module.service.Game.TypeService;
import com.example.module.service.GameTagRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class CategoryDeleteConsumer {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private GameTagRelationService gameTagRelationService;
    
    @Autowired
    private TypeService typeService;

    @RabbitListener(queues = RabbitMQConfig.CATEGORY_DELETE_QUEUE)
    public void handleCategoryDelete(CategoryDeleteMessage message) {
        log.info("接收到类型删除消息：{} - 类型ID: {}, 游戏数量: {}", 
                 message.getCategoryName(), message.getCategoryId(), message.getGameIds().size());
        log.info("消息详情：操作人={}, 时间戳={}", message.getOperator(), message.getTimestamp());
        
        try {
            int deletedGames = 0;
            int deletedRelations = 0;
            
            // 批量删除该类型下的所有游戏及其标签关联
            for (BigInteger gameId : message.getGameIds()) {
                try {
                    // 1. 先删除游戏与标签的关联关系
                    int relationResult = gameTagRelationService.deleteByGameId(gameId);
                    deletedRelations += relationResult;
                    
                    // 2. 删除游戏（逻辑删除）
                    int gameResult = gameService.delete(gameId);
                    if (gameResult > 0) {
                        deletedGames++;
                        log.info("已逻辑删除游戏：{}，所属类型：{}", gameId, message.getCategoryId());
                    }
                } catch (Exception e) {
                    log.error("删除游戏失败：{}，错误：{}", gameId, e.getMessage(), e);
                }
            }
            
            log.info("类型删除消息处理完成，共删除{}个游戏，{}条标签关联", deletedGames, deletedRelations);
            
            // 删除类型本身
            int result = typeService.delete(message.getCategoryId());
            if (result > 0) {
                log.info("已逻辑删除类型：{} - {}", message.getCategoryId(), message.getCategoryName());
            } else {
                log.warn("类型删除失败或已不存在：{} - {}", message.getCategoryId(), message.getCategoryName());
            }
            
        } catch (Exception e) {
            log.error("处理类型删除消息失败：{}", e.getMessage(), e);
            throw new RuntimeException("处理类型删除消息失败", e);
        }
    }
}