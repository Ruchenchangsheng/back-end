package com.hualiang.code;

import java.sql.Types;
import java.util.Collections;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class CodeGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/hotel", "root", "12345678")
                .globalConfig(builder -> {
                    builder.author("Hualiang") // 设置作者
                            .outputDir("C:\\Users\\64261\\Desktop\\Study\\JavaWeb\\hotel\\src\\main\\java"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("com.hualiang") // 设置父包名
                            .moduleName("hotel") // 设置父包模块名
                            .entity("pojo") // 设置实体类包名
                            .mapper("mapper") // 设置mapper包名
                            .service("service") // 设置service包名
                            .serviceImpl("service.impl") // 设置serviceImpl包名
                            // .controller("controller") // 设置controller包名
                            .xml("mapper") // 设置xml包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    "C:\\Users\\64261\\Desktop\\Study\\JavaWeb\\hotel\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("perm"); // 设置需要生成的表名
                    // .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 设置模板引擎
                .execute();
    }
}
