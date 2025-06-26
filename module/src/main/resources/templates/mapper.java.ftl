package ${package.Mapper};

import ${package.Entity}.${entity};
import java.math.BigInteger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ${table.mapperName} {
    // 根据ID查询操作
    @Select("SELECT * FROM ${table.name} WHERE id = <#noparse>#{</#noparse>${table.name}Id<#noparse>}</#noparse> AND is_deleted=0")
    ${entity} getById(BigInteger ${table.name}Id);

    // 根据ID提取记录
    @Select("SELECT * FROM ${table.name} WHERE id = <#noparse>#{</#noparse>${table.name}Id<#noparse>}</#noparse>")
    ${entity} extractById(BigInteger ${table.name}Id);

    // 插入记录
    int insert(@Param("${table.name}") ${entity} ${table.name});

    // 更新记录
    int update(@Param("${table.name}") ${entity} ${table.name});

    // 删除操作
    @Update("UPDATE ${table.name} SET update_time = <#noparse>#{</#noparse>updateTime<#noparse>}</#noparse>, is_deleted = 1 WHERE ${table.name}_id = <#noparse>#{</#noparse>${table.name}Id<#noparse>}</#noparse>")
    int delete(@Param("${table.name}Id") BigInteger ${table.name}Id, @Param("updateTime") Integer updateTime);
} 