package com.shiro.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class GeneratorUseRequest implements Serializable {

    private static final long serialVersionUID = 6671539372503319144L;
    /**
     * 生成器id
     */
    private Long id;

    /**
     * 数据模型
     */
    private Map<String, Object> dataModel;


}
