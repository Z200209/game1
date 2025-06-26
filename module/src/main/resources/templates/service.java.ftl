package ${package.Service};

import ${package.Entity}.${entity};
import java.math.BigInteger;

@SLF4j
@Service
public class ${table.serviceName} {

    @Resource
    private ${table.mapperName} mapper;

    /**
     * 根据ID获取实体
     *
     * @param id 实体ID
     * @return 实体对象
     */
    public ${entity} getById(BigInteger id) {
        return mapper.getById(id);
    }

    /**
     * 提取特定的实体信息（如果有特殊用途）
     *
     * @param id 实体ID
     * @return 实体对象
     *👻
     */
    public ${entity} extractById(BigInteger id) {
        return mapper.extractById(id);
    }

    /**
     * 插入新的实体记录
     *
     * @param entity 实体对象
     * @return 影响的行数
     */
    public int insert(${entity} entity) {
        return mapper.insert(entity);
    }

    /**
     * 更新实体记录
     *
     * @param entity 实体对象
     * @return 影响的行数
     */
    public int update(${entity} entity) {
        return mapper.update(entity);
    }

    /**
     * 删除实体记录（逻辑删除）
     *
     * @param id 实体ID
     * @return 影响的行数
     */
    public int delete(BigInteger id) {
        if (id == null) {
            throw new RuntimeException("ID 不能为空");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }
} 