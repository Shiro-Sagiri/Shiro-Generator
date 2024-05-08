package com.shiro.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiro.maker.meta.Meta;
import com.shiro.web.model.dto.generator.GeneratorQueryRequest;
import com.shiro.web.model.entity.Generator;
import com.shiro.web.model.vo.GeneratorVO;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ly179
 */
public interface GeneratorService extends IService<Generator> {

    void validGenerator(Generator generator, boolean add);

    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    GeneratorVO getGeneratorVO(Generator generator);

    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage);

    /**
     * 制作代码生成器
     * @param zipFilePath 模板zip文件路径
     * @param meta 元数据
     * @param response 响应
     */
    void makeGenerator(String zipFilePath, Meta meta, HttpServletResponse response);
}
