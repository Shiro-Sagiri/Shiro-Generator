package com.shiro.generator;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DynamicGenerator {

    /**
     * 使用Freemarker动态生成文件
     *
     * @param templatePath 模板路径
     * @param destPath     生成文件路径
     * @param model        模板数据
     * @throws IOException IO异常
     */
    public static void doGenerator(String templatePath, String destPath, Object model) throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File templateFile = new File(templatePath);
        configuration.setDirectoryForTemplateLoading(templateFile.getParentFile());
        configuration.setDefaultEncoding("UTF-8");

        String templateFileName = templateFile.getName();
        Template template = configuration.getTemplate(templateFileName);

        try (FileWriter writer = new FileWriter(destPath)) {
            template.process(model, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
