package com.shiro.web.model.dto.generator;

import com.shiro.web.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GeneratorAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfig modelConfig;

    private static final long serialVersionUID = 1L;
}
