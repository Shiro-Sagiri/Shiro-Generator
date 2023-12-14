package com.shiro.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class StaticGenerator {

    /**
     * 拷贝文件(通过hutool实现,不会覆盖已有文件)
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     */
    public static void copyFilesByHutool(String sourcePath, String targetPath) {
        FileUtil.copy(sourcePath, targetPath, false);
    }

    /**
     * 拷贝文件(通过递归实现)
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     */
    public static void copyFilesByRecursive(String sourcePath, String targetPath) {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        try {
            copyFileByRecursive(sourceFile, targetFile);
        } catch (IOException e) {
            throw new IORuntimeException("复制失败!");
        }
    }

    /**
     * 使用递归方式完成复制
     *
     * @param sourceFile 源文件路径
     * @param targetFile 目标文件路径
     * @throws IOException io异常
     */
    private static void copyFileByRecursive(File sourceFile, File targetFile) throws IOException {
        //区分文件和目录
        if (sourceFile.isDirectory()) {
            //若为目录,则先创建目标目录
            File dest = new File(targetFile, sourceFile.getName());
            if (!dest.exists()) {
                boolean res = dest.mkdirs();
                if (!res) {
                    throw new IORuntimeException("目标目录创建失败");
                }
            }
            //获取目录下的所有子文件
            File[] files = sourceFile.listFiles();
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            //递归拷贝下一层文件
            for (File file : files) {
                copyFileByRecursive(file, dest);
            }
        } else {
            //若为文件,直接拷贝到目标目录下
            Path destPath = targetFile.toPath().resolve(sourceFile.getName());
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
