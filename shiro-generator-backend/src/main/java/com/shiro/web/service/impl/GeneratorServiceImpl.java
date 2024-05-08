package com.shiro.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiro.maker.generator.main.GenerateTemplate;
import com.shiro.maker.generator.main.ZipGenerator;
import com.shiro.maker.meta.Meta;
import com.shiro.web.common.ErrorCode;
import com.shiro.web.constant.CommonConstant;
import com.shiro.web.exception.BusinessException;
import com.shiro.web.exception.ThrowUtils;
import com.shiro.web.manager.MinioManager;
import com.shiro.web.mapper.GeneratorMapper;
import com.shiro.web.model.dto.generator.GeneratorQueryRequest;
import com.shiro.web.model.entity.Generator;
import com.shiro.web.model.entity.User;
import com.shiro.web.model.vo.GeneratorVO;
import com.shiro.web.model.vo.UserVO;
import com.shiro.web.service.GeneratorService;
import com.shiro.web.service.UserService;
import com.shiro.web.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author ly179
 */
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
        implements GeneratorService {
    @Resource
    private UserService userService;
    @Resource
    private MinioManager minioManager;

    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();
        String basePackage = generator.getBasePackage();
        String author = generator.getAuthor();
        String tags = generator.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description, tags, author, basePackage), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest 查询条件
     * @return 查询包装类
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        Long id = generatorQueryRequest.getId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        List<String> tags = generatorQueryRequest.getTags();
        Integer status = generatorQueryRequest.getStatus();
        Long userId = generatorQueryRequest.getUserId();
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("name", searchText).or().like("description", searchText).or().like("author", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public GeneratorVO getGeneratorVO(Generator generator) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        return generatorVO;
    }

    @Override
    public void makeGenerator(String zipFilePath, Meta meta, HttpServletResponse response) {
        //模板文件压缩包路径判空
        if (StrUtil.isBlank(zipFilePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "压缩包不存在");
        }
        //创建工作空间,用于存放模板文件,临时文件等
        String projectPath = System.getProperty("user.dir"); //工作空间
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6); //生成随机id
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        //下载模板文件压缩包到本地
        String localZipFilePath = tempDirPath + "/project.zip";
        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }
        try {
            minioManager.downloadFile(zipFilePath, localZipFilePath);
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩包下载失败!");
        }
        //解压模板文件
        File unzipDistFile = ZipUtil.unzip(localZipFilePath);
        //构造meta对象
        String sourceRootPath = unzipDistFile.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        String outputPath = String.format("%s/generated/%s", tempDirPath, meta.getName());
        //调用制作器
        GenerateTemplate generator = new ZipGenerator();
        try {
            generator.doGenerate(meta, outputPath);
        } catch (IOException | InterruptedException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成器制作失败!");
        }
        //返回生成结果
        String suffix = "-dist.zip";
        String distFileName = meta.getName() + suffix;
        String distFilePath = outputPath + distFileName;
        response.setContentType("application/octet-stream;charset=uft-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + distFileName);
        try {
            Files.copy(Paths.get(distFilePath), response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败!");
        }
        //异步清理临时文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }
}




