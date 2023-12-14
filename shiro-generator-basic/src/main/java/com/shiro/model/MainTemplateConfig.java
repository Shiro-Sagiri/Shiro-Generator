package com.shiro.model;

import lombok.Data;

@Data
public class MainTemplateConfig {

    /**
     * 是否生成循环
     */
    private Boolean loop;

    /**
     * 作者注释
     */
    private String author = "shiro";

    /**
     * 输出信息
     */
    private String outputText = "Sum = ";
}
