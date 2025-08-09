package com.example.module.mq.task;

import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.mq.message.CategoryDeleteMessage;
import com.example.module.service.Game.GameService;
import com.example.module.service.Game.TypeService;
import com.example.module.service.GameTagRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类删除补偿任务
 * 用于处理可能失败的删除操作
 */
@Slf4j
@Component
public class CategoryDeleteCompensationTask {

    @Autowired
    private TypeService typeService;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private GameTagRelationService gameTagRelationService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 每5分钟执行一次补偿检查
     */
    @Scheduled(fixedDelay = 300000)
    public void compensateFailedDeletions() {
        log.info("开始执行分类删除补偿任务");
        
        try {
            // 这里可以添加更复杂的补偿逻辑
            // 例如检查数据库中是否还有已标记删除但仍存在的记录
            
            log.info("分类删除补偿任务执行完成");
        } catch (Exception e) {
            log.error("分类删除补偿任务执行失败", e);
        }
    }

    /**
     * 手动触发补偿任务
     */
    public void manualCompensate(BigInteger typeId) {
        log.info("手动触发分类删除补偿任务: {}", typeId);
        
        try {
            Type type = typeService.getById(typeId);
            if (type == null) {
                log.warn("分类不存在: {}", typeId);
                return;
            }
            
            // 检查该分类下是否还有游戏
            List<Game> games = gameService.getAllGameByTypeId(typeId);
            if (games.isEmpty()) {
                log.info("分类下无游戏，无需补偿: {}", typeId);
                return;
            }
            
            // 重新发送删除消息
            List<BigInteger> gameIds = games.stream().map(Game::getId).collect(Collectors.toList());
            CategoryDeleteMessage message = new CategoryDeleteMessage()
                    .setCategoryId(typeId)
                    .setCategoryName(type.getTypeName())
                    .setGameIds(gameIds)
                    .setTimestamp((int) (System.currentTimeMillis() / 1000))
                    .setOperator(BigInteger.ZERO); // 系统补偿
            
            rabbitTemplate.convertAndSend("category.delete.exchange", "category.delete.routing", message);
            log.info("补偿消息已发送: {}", typeId);
            
        } catch (Exception e) {
            log.error("手动补偿任务执行失败: {}", typeId, e);
        }
    }
}