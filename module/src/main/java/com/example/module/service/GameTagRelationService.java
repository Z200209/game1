package com.example.module.service;

import com.example.module.config.data.DataSourceType;
import com.example.module.entity.GameTagRelation;
import com.example.module.mapper.GameTagRelationMapper;
import com.example.module.annotations.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
public class GameTagRelationService {

    @Resource
    private GameTagRelationMapper mapper;

    /**
     * 根据游戏ID获取标签关系
     */
    @DataSource(DataSourceType.SLAVE)
    public List<GameTagRelation> getByGameId(BigInteger gameId) {
        return mapper.getByGameId(gameId);
    }
    
    /**
     * 根据游戏ID和标签ID获取关系
     */
    @DataSource(DataSourceType.SLAVE)
    public GameTagRelation getByGameIdAndTagId(BigInteger gameId, BigInteger tagId) {
        return mapper.getByGameIdAndTagId(gameId, tagId);
    }

    /**
     * 根据游戏ID获取标签ID列表
     */
    @DataSource(DataSourceType.SLAVE)
    public List<BigInteger> getTagIdsByGameId(BigInteger gameId) {
        return mapper.getTagIdsByGameId(gameId);
    }

    /**
     * 创建游戏标签关系
     */
    @DataSource(DataSourceType.MASTER)
    public int create(BigInteger gameId, BigInteger tagId) {
        // 检查关系是否已经存在
        GameTagRelation exist = getByGameIdAndTagId(gameId, tagId);
        if (exist != null) {
            return 0; // 已存在，不需要创建
        }
        
        // 创建新关系
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        GameTagRelation relation = new GameTagRelation()
            .setGameId(gameId)
            .setTagId(tagId)
            .setCreateTime(currentTime)
            .setUpdateTime(currentTime)
            .setIsDeleted(0);
        
        return mapper.insert(relation);
    }
    
    /**
     * 批量删除不在标签ID列表中的关系
     */
    @DataSource(DataSourceType.MASTER)
    public int deleteNotInTagIds(BigInteger gameId, List<BigInteger> tagIds) {
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        return mapper.batchDeleteByGameIdAndNotInTagIds(gameId, tagIds, currentTime);
    }
}