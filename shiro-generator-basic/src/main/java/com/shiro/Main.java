package com.shiro;


import com.shiro.generator.StaticGenerator;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        //获取项目根路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        //获取模板路径
        String sourcePath = new File(parentFile, "shiro-generator-demo-projects/acm-template").getAbsolutePath();
        //复制模板到此项目目录中
//        StaticGenerator.copyFilesByHutool(sourcePath, projectPath);
        StaticGenerator.copyFilesByRecursive(sourcePath, projectPath);
    }
}