package com.shiro.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shiro.maker.meta.Meta;
import com.shiro.maker.meta.enums.FileGenerateTypeEnum;
import com.shiro.maker.meta.enums.FileTypeEnum;
import com.shiro.maker.template.enums.FileFilterRangeEnum;
import com.shiro.maker.template.enums.FileFilterRuleEnum;
import com.shiro.maker.template.model.FileFilterConfig;
import com.shiro.maker.template.model.TemplateMakerFileConfig;
import com.shiro.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 */
public class TemplateMaker {

    /**
     * 生成模板
     *
     * @param newMeta                  元信息
     * @param originProjectPath        原始项目路径
     * @param templateMakerFileConfig  模板文件配置
     * @param templateMakerModelConfig 模板模型配置
     * @param id                       id
     * @return id
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, Long id) {
        //没有id,生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        //有id
        //复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 输入信息

        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());
        List<Meta.ModelConfig.ModelInfo> newModelInfoList;
        //如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            String condition = modelGroupConfig.getCondition();
            String groupName = modelGroupConfig.getGroupName();
            String groupKey = modelGroupConfig.getGroupKey();

            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setCondition(condition);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setGroupKey(groupKey);
            // 文件全放到一个分组内
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList = new ArrayList<>();
            newModelInfoList.add(groupModelInfo);
        } else {
            newModelInfoList = new ArrayList<>(inputModelInfoList);
        }

        // 1.项目的基本信息
        File tempFile = new File(templatePath);
        templatePath = tempFile.getAbsolutePath();
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        for (TemplateMakerFileConfig.FileInfoConfig fileInputConfig : fileInfoConfigList) {
            String fileInputPath = fileInputConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;
            //传入绝对路径
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInputConfig.getFileFilterConfigList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        //如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String condition = fileGroupConfig.getCondition();
            String groupName = fileGroupConfig.getGroupName();
            String groupKey = fileGroupConfig.getGroupKey();

            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setGroupKey(groupKey);
            // 文件全放到一个分组内
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }

        //生成配置为文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        if (FileUtil.exist(metaOutputPath)) {
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            // 1.追加参数配置
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            //配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            //1.构造配置参数对象
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);

            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }
        // 输入元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }

    /**
     * 生成单个文件模板
     *
     * @param templateMakerModelConfig 模板模型配置
     * @param sourceRootPath           源文件路径
     * @param inputFile                输入文件
     * @return 文件信息
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile) {
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        // 2.输入文件信息
        String fileOutputPath = fileInputPath + ".ftl";

        //使用字符串替换,生成模板文件
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        String fileContent;
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", fieldName);
            } else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, fieldName);
            }
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        //是否和源文件一致,若一致,则静态生成
        if (newFileContent.equals(fileContent)) {
            fileInfo.setOutputPath(fileInputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            //输入模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList 文件信息列表
     * @return 去重后的文件信息列表
     */
    public static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1. 将所有文件配置(fileConfig)分为有分组的和无分组的
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // 2. 对于有分组的文件配置,如果有相同的分组,同组内的文件合并(merge),不同分组可同时保留
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>(); //合并后的map

        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, value -> value, (exist, replacement) -> replacement)).values());
            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }
        // 3. 创建新的文件配置列表(结果列表),先将合并后的分组添加到结果列表
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());
        // 4. 再将无分组的文件配置列表添加结果列表
        resultList.addAll(new ArrayList<>(fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, value -> value, (exist, replacement) -> replacement)).values()));
        return resultList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList 模型信息列表
     * @return 去重后的模型信息列表
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 1. 将所有模型配置(modelConfig)分为有分组的和无分组的
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        // 2. 对于有分组的模型配置,如果有相同的分组,同组内的模型合并(merge),不同分组可同时保留
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>(); //合并后的map

        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, value -> value, (exist, replacement) -> replacement)).values());
            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }
        // 3. 创建新的模型配置列表(结果列表),先将合并后的分组添加到结果列表
        ArrayList<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());
        // 4. 再将无分组的模型配置列表添加结果列表
        resultList.addAll(new ArrayList<>(modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, value -> value, (exist, replacement) -> replacement)).values()));
        return resultList;
    }

    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath) + File.separator + "shiro-generator-demo-projects/springboot-init";
        String fileInputPath = "src/main/java/com/yupi/springbootinit";
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";
        String fileInputPath2 = "src/main/resources/application.yml";

        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        String searchStr = "BaseResponse";

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFileFilterConfigList(fileFilterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(fileInputPath2);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig2, fileInfoConfig1);

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        // 文件分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupName("测试分组");
        fileGroupConfig.setGroupKey("test");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, null);
        System.out.println(id);
    }
}
