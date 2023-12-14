package com.shiro.generator;

import com.shiro.model.MainTemplateConfig;

import java.io.File;
import java.io.IOException;

public class MainGenerator {

    /**
     * 通过模板动静结合生成文件
     *
     * @param model 模板数据
     * @throws IOException IO异常
     */
    public static void doGenerator(Object model) throws IOException {
        String projectPath = System.getProperty("user.dir"); //当前项目路径
        File parentFile = new File(projectPath).getParentFile(); // 获取项目根路径
        String templatePath = new File(parentFile, "shiro-generator-demo-projects/acm-template").getAbsolutePath(); // 获取模板路径
        //生成静态文件
        StaticGenerator.copyFilesByHutool(templatePath, projectPath);
        //生成动态文件
        templatePath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String destPath = projectPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
        DynamicGenerator.doGenerator(templatePath, destPath, model);
    }

    public static void main(String[] args) throws IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("Shiro");
        mainTemplateConfig.setLoop(true);
        mainTemplateConfig.setOutputText("结果 = ");
        doGenerator(mainTemplateConfig);
    }
}
