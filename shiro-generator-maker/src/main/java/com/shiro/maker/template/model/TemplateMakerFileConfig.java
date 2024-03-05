package com.shiro.maker.template.model;

import com.shiro.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupConfig fileGroupConfig;

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig{
        private String path;
        private List<FileFilterConfig> fileFilterConfigList;
    }

    @Data
    @NoArgsConstructor
    public static class FileGroupConfig{
        private String condition;
        private String groupName;
        private String groupKey;
    }
}
