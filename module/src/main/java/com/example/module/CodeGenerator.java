package com.example.module;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成器
 * 生成MyBatis相关代码到entity、mapper、service目录
 */
public class CodeGenerator {
    public static void main(String[] args) {
        // 项目路径
        String projectPath = System.getProperty("user.dir");
        projectPath = projectPath.replace('\\', '/');
        System.out.println("项目路径: " + projectPath);
        
        // 数据库配置
        String url = "jdbc:mysql://localhost:3306/game";
        String username = "root";
        String password = "Password";
        
        // 模板路径（使用classpath方式）
        String javaPath = projectPath + "/src/main/java";
        String resourcePath = projectPath + "/src/main/resources";
        
        // 创建输出目录
        new File(resourcePath + "/mapper").mkdirs();
        
        // 配置输出路径映射
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.entity, javaPath + "/com/example/module/entity");
        pathInfo.put(OutputFile.mapper, javaPath + "/com/example/module/mapper");
        pathInfo.put(OutputFile.service, javaPath + "/com/example/module/service");
        pathInfo.put(OutputFile.xml, resourcePath + "/mapper");
        
        // 包名配置
        String basePackage = "com.example.module";
        
        FastAutoGenerator.create(url, username, password)
            .globalConfig(builder -> {
                builder.author("system")                 // 设置作者
                       .outputDir(javaPath)              // 指定输出目录
                       .disableOpenDir()                 // 禁止打开输出目录
                       .fileOverride();                  // 覆盖已有文件
            })
            .packageConfig(builder -> {
                builder.parent(basePackage)              // 设置父包名
                       .entity("entity")                 // 设置entity包名
                       .mapper("mapper")                 // 设置mapper包名
                       .service("service")               // 设置service包名
                       .xml("mapper")                    // 设置xml包名
                       .pathInfo(pathInfo);              // 设置输出路径映射
            })
            .templateConfig(builder -> {
                // 禁用所有默认模板
                builder.disable(TemplateType.CONTROLLER)
                       .disable(TemplateType.ENTITY)
                       .disable(TemplateType.MAPPER)
                       .disable(TemplateType.SERVICE)
                       .disable(TemplateType.SERVICE_IMPL)
                       .disable(TemplateType.XML);
            })
            .injectionConfig(builder -> {
                // 使用自定义模板文件
                Map<String, String> customFile = new HashMap<>();
                customFile.put("entity.java", "/templates/entity.java.ftl");
                customFile.put("mapper.java", "/templates/mapper.java.ftl");
                customFile.put("service.java", "/templates/service.java.ftl");
                customFile.put("mapper.xml", "/templates/mapper.xml.ftl");
                
                builder.customFile(customFile);
            })
            .strategyConfig(builder -> {
                builder.addInclude("tag")                // 设置需要生成的表名
                       .addTablePrefix("t_", "c_")       // 设置过滤表前缀
                    
                    // Entity策略配置
                    .entityBuilder()
                    .enableFileOverride()
                       .formatFileName("%s")             // 实体类命名格式
                    
                    // Mapper策略配置
                    .mapperBuilder()
                    .enableFileOverride()
                       .formatMapperFileName("%sMapper") // Mapper接口命名格式
                       .formatXmlFileName("%sMapper")    // Mapper XML命名格式
                    
                    // Service策略配置
                    .serviceBuilder()
                    .enableFileOverride()
                       .formatServiceFileName("%sService"); // Service命名格式
            })
            .templateEngine(new FreemarkerTemplateEngine())
            .execute();
        
        System.out.println("代码生成完成！");
        System.out.println("生成位置：");
        System.out.println("实体类：" + pathInfo.get(OutputFile.entity));
        System.out.println("Mapper接口：" + pathInfo.get(OutputFile.mapper));
        System.out.println("Service类：" + pathInfo.get(OutputFile.service));
        System.out.println("Mapper XML：" + pathInfo.get(OutputFile.xml));
    }
}
