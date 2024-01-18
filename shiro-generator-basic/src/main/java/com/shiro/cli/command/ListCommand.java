package com.shiro.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        String templatePath = new File(parentFile, "shiro-generator-demo-projects/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(templatePath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
