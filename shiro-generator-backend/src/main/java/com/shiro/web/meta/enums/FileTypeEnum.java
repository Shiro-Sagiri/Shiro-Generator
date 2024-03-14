package com.shiro.web.meta.enums;

import lombok.Getter;

@Getter
public enum FileTypeEnum {

    DIR("目录", "dir"),
    FILE("文件", "file"),
    GROUP("分组", "group");

    private final String text;

    private final String value;

    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

}