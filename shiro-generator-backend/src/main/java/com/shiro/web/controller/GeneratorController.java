package com.shiro.web.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiro.web.annotation.AuthCheck;
import com.shiro.web.common.BaseResponse;
import com.shiro.web.common.DeleteRequest;
import com.shiro.web.common.ErrorCode;
import com.shiro.web.common.ResultUtils;
import com.shiro.web.constant.UserConstant;
import com.shiro.web.exception.BusinessException;
import com.shiro.web.exception.ThrowUtils;
import com.shiro.web.meta.Meta;
import com.shiro.web.model.dto.generator.GeneratorAddRequest;
import com.shiro.web.model.dto.generator.GeneratorQueryRequest;
import com.shiro.web.model.dto.generator.GeneratorUpdateRequest;
import com.shiro.web.model.entity.Generator;
import com.shiro.web.model.entity.User;
import com.shiro.web.model.vo.GeneratorVO;
import com.shiro.web.service.GeneratorService;
import com.shiro.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest 生成器新增请求
     * @param request             请求
     * @return 是否成功
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        List<String> tags = generatorAddRequest.getTags();
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        if (fileConfig != null) {
            generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        }
        if (modelConfig != null) {
            generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        }
        generator.setAuthor(userService.getLoginUser(request).getUserName());
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(generator.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(generatorService.removeById(id));
    }

    /**
     * 更新
     *
     * @param generatorUpdateRequest 生成器更新请求
     * @return 是否成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest, HttpServletRequest request) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        if (fileConfig != null) {
            generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        }
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        if (modelConfig != null) {
            generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        }
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        User user = userService.getLoginUser(request);
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 生成器
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest 查询条件
     * @return 生成器列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        Page<Generator> generatorPage = getGeneratorPage(generatorQueryRequest);
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest 查询条件
     * @return 生成器列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        Page<Generator> generatorPage = getGeneratorPage(generatorQueryRequest);
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest 查询条件
     * @param request               请求
     * @return 生成器列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        Page<Generator> generatorPage = getGeneratorPage(generatorQueryRequest);
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage));
    }

    private Page<Generator> getGeneratorPage(GeneratorQueryRequest generatorQueryRequest) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        return generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
    }
    // endregion
}
