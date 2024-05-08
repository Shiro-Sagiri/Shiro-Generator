package com.shiro.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiro.maker.meta.Meta;
import com.shiro.web.annotation.AuthCheck;
import com.shiro.web.common.BaseResponse;
import com.shiro.web.common.DeleteRequest;
import com.shiro.web.common.ErrorCode;
import com.shiro.web.common.ResultUtils;
import com.shiro.web.constant.UserConstant;
import com.shiro.web.exception.BusinessException;
import com.shiro.web.exception.ThrowUtils;
import com.shiro.web.manager.MinioManager;
import com.shiro.web.model.dto.generator.*;
import com.shiro.web.model.entity.Generator;
import com.shiro.web.model.entity.User;
import com.shiro.web.model.vo.GeneratorVO;
import com.shiro.web.service.GeneratorService;
import com.shiro.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private MinioManager minioManager;

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

    @GetMapping("/download")
    public void downloadGeneratorById(Long id, HttpServletRequest request, HttpServletResponse response) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String distPath = getGeneratorDistPath(id);

        User loginUser = userService.getLoginUser(request);
        log.info("用户 {} 下载了 {}", loginUser, distPath);

        try (InputStream fileInputStream = minioManager.getFileInputStream(distPath)) {
            byte[] fileBytes = IOUtils.toByteArray(fileInputStream);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + distPath);
            response.getOutputStream().write(fileBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download failure,file path = {}", distPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }
    }

    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request, HttpServletResponse response) {
        Long generatorId = generatorUseRequest.getId();
        //需要登录
        userService.getLoginUser(request);
        String distPath = getGeneratorDistPath(generatorId);
        //临时工作空间,用于暂存生成器压缩包等资源
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, generatorId);
        String tempDistZip = tempDirPath + "/dist.zip";
        if (!FileUtil.exist(tempDistZip)) {
            FileUtil.touch(tempDistZip);
        }
        try {
            minioManager.downloadFile(distPath, tempDistZip);
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败!");
        }
        File unzipDir = ZipUtil.unzip(tempDistZip);
        //写入Json配置文件用于生成代码
        String dataModelFilePath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(generatorUseRequest.getDataModel());
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);
        //获取生成脚本
        List<File> scriptFileList = FileUtil.loopFiles(unzipDir, 2, null).stream()
                .filter(file -> file.isFile() && file.getName().contains("generator"))
                .collect(Collectors.toList());

        //执行脚本
        //获取用户操作系统,区分不同的命令
        String osName = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        String commandArgs = "json-generate --filePath=" + dataModelFilePath;
        File scriptFile;
        if (osName.contains("win")) {
            // Windows系统
            scriptFile = scriptFileList.stream()
                    .filter(file -> "generator.bat".equals(file.getName()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到可执行脚本文件"));
            processBuilder = new ProcessBuilder("cmd.exe", "/c", scriptFile.getAbsolutePath(), commandArgs);
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            // Unix/Linux/Mac系统,非window系统添加可执行权限
            scriptFile = scriptFileList.stream()
                    .filter(file -> "generator".equals(file.getName()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到可执行脚本文件"));
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            try {
                Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            processBuilder = new ProcessBuilder("/bin/bash", "-c", scriptFile.getAbsolutePath(), commandArgs);
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持此操作系统: " + osName);
        }
        File scriptDir = scriptFile.getParentFile();
        processBuilder.directory(scriptDir);
        try {
            Process process = processBuilder.start();
            //读取命令的输出
            InputStream processInputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            //等待命令执行成功
            int exitCode = process.waitFor();
            System.out.println("命令执行结束: " + exitCode);
            //下载文件
            String generatedPath = scriptDir.getAbsolutePath() + File.separator + "generated";
            String resultPath = tempDirPath + File.separator + "result.zip";
            File resultFile = ZipUtil.zip(generatedPath, resultPath);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
            Files.copy(resultFile.toPath(), response.getOutputStream());
        } catch (IOException | InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //异步清理临时文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    private String getGeneratorDistPath(Long generatorId) {
        Generator generator = generatorService.getById(generatorId);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //获取产物包路径
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }
        return distPath;
    }

    /**
     * 在线制作代码生成器
     *
     * @param generatorMakeRequest 生成器制作请求
     * @param response             响应
     */
    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest, HttpServletResponse response) {
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();
        generatorService.makeGenerator(zipFilePath, meta, response);
    }
}
