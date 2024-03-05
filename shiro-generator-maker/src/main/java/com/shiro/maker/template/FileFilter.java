package com.shiro.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.shiro.maker.template.enums.FileFilterRangeEnum;
import com.shiro.maker.template.enums.FileFilterRuleEnum;
import com.shiro.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileFilter {

    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        // 所有过滤器校验结束后的结果
        boolean result = true;

        if (CollUtil.isEmpty(fileFilterConfigList)) {
            return true;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if (fileFilterRangeEnum == null) {
                continue;
            }

            // 要过滤的原内容
            String content = fileName;
            switch (fileFilterRangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if (fileFilterRuleEnum == null) {
                continue;
            }

            switch (fileFilterRuleEnum) {
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                case CONTAINS:
                    result = content.contains(value);
                    break;
                default:
            }

            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * 文件过滤,过滤一个文件或目录
     *
     * @param filePath            文件路径
     * @param fileFilterConfigList 文件过滤配置
     * @return 过滤后的文件列表
     */
    public static List<File> doFilter(String filePath, List<FileFilterConfig> fileFilterConfigList) {
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(file -> doSingleFileFilter(fileFilterConfigList,file))
                .collect(Collectors.toList());
    }
}
