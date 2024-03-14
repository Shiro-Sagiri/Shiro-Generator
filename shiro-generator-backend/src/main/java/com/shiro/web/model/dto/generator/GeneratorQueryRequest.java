package com.shiro.web.model.dto.generator;

import com.shiro.web.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GeneratorQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private String description;

    /**
     * 名称
     */
    private String name;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建用户 id
     */
    private Long userId;

    private String searchText;

    private static final long serialVersionUID = 1L;
}
