package com.shiro.maker.generator;


import com.shiro.maker.generator.main.GenerateTemplate;

public class MainGenerator extends GenerateTemplate {
    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputFilePath) {
        System.out.println("不需要生成精简版");
    }
}
