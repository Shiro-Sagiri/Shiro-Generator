package com.shiro.maker.generator.file;

import cn.hutool.core.io.FileUtil;


public class StaticFileGenerator {

    /**
     * 拷贝文件(通过hutool实现,不会覆盖已有文件)
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     */
    public static void copyFilesByHutool(String sourcePath, String targetPath) {
        FileUtil.copy(sourcePath, targetPath, false);
    }

}
