package com.example.module.service.Game;

import com.example.module.entity.Type;
import com.example.module.mapper.TypeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Service
public class TypeService {
    @Resource
    private TypeMapper mapper;

    public Type getById(BigInteger id) {
        return mapper.getById(id);
    }

    public List<Type> getTypeByIds(Set<BigInteger> typeIdSet) {
        StringBuilder typeIdList = new StringBuilder();
        for (BigInteger bigInteger : typeIdSet) {
            if (!typeIdList.isEmpty()){
                typeIdList.append(",");
            }
            typeIdList.append(bigInteger.toString());
        }
        String ids = typeIdList.toString();

        return mapper.getTypeByIds(ids);
    }



    public Type extractById(BigInteger id) {
        return mapper.extractById(id);
    }

    @Transactional
    public int insert(Type type) {
        return mapper.insert(type);
    }

    @Transactional
    public int update(Type type) {
        return mapper.update(type);
    }
    @Transactional
    public int delete(BigInteger id) {
        if (id == null){
            throw new RuntimeException("id 不能为空");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }

    public List<Type> getAllType(String keyword) {
        return mapper.getAllType(keyword);
    }

    public List<Type> getParentTypeList( String keyword) {
        return mapper.getParentTypeList(keyword);
    }

    public List<Type> getChildrenList(BigInteger id) {
        return mapper.getChildrenTypeList(id);
    }

    public Integer getTotalCount(String keyword) {
        return mapper.getTotalCount(keyword);
    }
    @Transactional
    public BigInteger edit (BigInteger id, String typeName,String image,BigInteger parentId) {
        if (typeName == null || typeName.isEmpty()) {
            throw new RuntimeException("typeName 不能为空");
        }
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("images 不能为空");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        Type type = new Type();
        type.setTypeName(typeName);
        type.setParentId(parentId);
        type.setImage(image);
        type.setUpdateTime(time);
        if (id == null){
            type.setCreateTime(time);
            type.setIsDeleted(0);
            int result = insert(type);
            if (result == 0){
                throw new RuntimeException("插入失败");
            }
        }
        else {
            type.setId(id);
            int result = update(type);
            if (result == 0){
                throw new RuntimeException("更新失败");
            }

        }
        return type.getId();
    }

    public List<BigInteger> getTypeIdList(String keyword) {
        return mapper.getTypeIdList(keyword);
    }

    public List<Type> getRootTypes() {
        return mapper.getRootTypes();
    }

}

