package com.shiro.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.shiro.maker.meta.enums.FileGenerateTypeEnum;
import com.shiro.maker.meta.enums.FileTypeEnum;
import com.shiro.maker.meta.enums.ModelTypeEnum;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MetaValidator {

    /**
     * 校验和填充元信息
     *
     * @param meta 元信息
     */
    public static void doValidateAndFill(Meta meta) {
        validateAndFillMetaRoot(meta);
        validateAndFillFileConfig(meta);
        validateAndFillModelConfig(meta);
    }

    /**
     * modelConfig 校验和默认值
     *
     * @param meta 元信息
     */
    private static void validateAndFillModelConfig(Meta meta) {
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
        if (!CollUtil.isNotEmpty(models)) {
            return;
        }
        for (Meta.ModelConfig.ModelInfo model : models) {
            String fieldName = model.getFieldName();
            //为group不校验
            if (StrUtil.isNotEmpty(model.getGroupKey())) {
                //生成中间参数 "--author", "--outputText"
                List<Meta.ModelConfig.ModelInfo> subModels = model.getModels();
                String allArgsStr = subModels.stream().map(subModel -> String.format("\"--%s\"", subModel.getFieldName())).collect(Collectors.joining(", "));
                model.setAllArgsStr(allArgsStr);
                continue;
            }
            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("未填写 fieldName");
            }
            String modelInfoType = model.getType();
            modelInfoType = StrUtil.blankToDefault(modelInfoType, ModelTypeEnum.STRING.getValue());
            model.setType(modelInfoType);
        }
    }

    /**
     * fileConfig 校验和默认值
     *
     * @param meta 元信息
     */
    private static void validateAndFillFileConfig(Meta meta) {
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }
        String inputRootPath = fileConfig.getInputRootPath();
        Path rootPath = Paths.get(sourceRootPath);
        String defaultInputRootPath = ".source/" + FileUtil.getLastPathEle(rootPath).getFileName().toString();
        inputRootPath = StrUtil.emptyToDefault(inputRootPath, defaultInputRootPath);
        fileConfig.setInputRootPath(inputRootPath);
        String outputRootPath = fileConfig.getOutputRootPath();
        outputRootPath = StrUtil.emptyToDefault(outputRootPath, FileUtil.getLastPathEle(rootPath).getFileName().toString());
        fileConfig.setOutputRootPath(outputRootPath);
        String fileConfigType = fileConfig.getType();
        fileConfigType = StrUtil.emptyToDefault(fileConfigType, FileTypeEnum.DIR.getValue());
        fileConfig.setType(fileConfigType);
        List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
        if (!CollUtil.isNotEmpty(files)) {
            return;
        }
        for (Meta.FileConfig.FileInfo fileInfo : files) {
            String inputPath = fileInfo.getInputPath();
            //若文件类型为组,则特殊校验
            if (FileTypeEnum.GROUP.getValue().equals(fileInfo.getType())) {
                continue;
            }
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("未填写 inputPath");
            }
            String outputPath = fileInfo.getOutputPath();
            outputPath = StrUtil.emptyToDefault(outputPath, inputPath);
            fileInfo.setOutputPath(outputPath);
            String type = fileInfo.getType();
            type = StrUtil.blankToDefault(type, StrUtil.isBlank(FileUtil.getSuffix(inputPath)) ? FileTypeEnum.DIR.getValue() : FileTypeEnum.FILE.getValue());
            fileInfo.setType(type);
            String generateType = fileInfo.getGenerateType();
            generateType = StrUtil.blankToDefault(generateType, inputPath.endsWith(".ftl") ? FileGenerateTypeEnum.DYNAMIC.getValue() : FileGenerateTypeEnum.STATIC.getValue());
            fileInfo.setGenerateType(generateType);
        }
    }

    /**
     * 基础信息校验和默认值
     *
     * @param meta 元信息
     */
    private static void validateAndFillMetaRoot(Meta meta) {
        String name = StrUtil.blankToDefault(meta.getName(), "my_generator");
        meta.setName(name);
        String description = StrUtil.blankToDefault(meta.getDescription(), "我的模板代码生成器");
        meta.setDescription(description);
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.myGenerator");
        meta.setBasePackage(basePackage);
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        meta.setVersion(version);
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "shiro");
        meta.setAuthor(author);
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());
        meta.setCreateTime(createTime);
    }
}
