package com.shiro.web.model.dto.generator;

import com.shiro.maker.meta.Meta;
import lombok.Data;

import java.io.Serializable;

/**
 * 在线制作代码生成器
 */
@Data
public class GeneratorMakeRequest implements Serializable {

    private String zipFilePath;

    private Meta meta;

    private static final long serialVersionUID = 1L;
}
