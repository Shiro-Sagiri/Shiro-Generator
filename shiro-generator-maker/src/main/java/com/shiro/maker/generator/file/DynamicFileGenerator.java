package com.shiro.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DynamicFileGenerator {

    /**
     * 使用生成路径生成文件
     *
     * @param relativePath 相对路径
     * @param outputPath   输出路径
     * @param model        模板数据
     */
    public static void doGenerate(String relativePath, String outputPath, Object model) throws IOException, TemplateException {
        //FreeMaker 配置对象
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        //获取包路径及文件名
        int splitIndex = relativePath.lastIndexOf("/");
        String basePackagePath = relativePath.substring(0, splitIndex);
        String TemplateName = relativePath.substring(splitIndex + 1);
        //配置模板类加载器
        ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(DynamicFileGenerator.class, basePackagePath);
        configuration.setTemplateLoader(classTemplateLoader);
        //设置模板文件的字符集
        configuration.setDefaultEncoding("UTF-8");
        //通过配置对象,创建指定模板对象
        Template template = configuration.getTemplate(TemplateName);
        //判断输出路径是否存在,若不存在则创建
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        //根据模板对象生成文件
        try (Writer writer = new FileWriter(outputPath)) {
            template.process(model, writer);
        }
    }

    /**
     * 使用Freemarker动态生成文件
     *
     * @param templatePath 模板路径
     * @param destPath     生成文件路径
     * @param model        模板数据
     * @throws IOException IO异常
     */
    @Deprecated
    public static void doGenerateByPath(String templatePath, String destPath, Object model) throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File templateFile = new File(templatePath);
        configuration.setDirectoryForTemplateLoading(templateFile.getParentFile());
        configuration.setDefaultEncoding("UTF-8");

        String templateFileName = templateFile.getName();
        Template template = configuration.getTemplate(templateFileName);

        //如果文件不存在,则创建文件
        if (!FileUtil.exist(destPath)) {
            FileUtil.touch(destPath);
        }

        try (FileWriter writer = new FileWriter(destPath)) {
            template.process(model, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
