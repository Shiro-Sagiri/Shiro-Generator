package com.shiro.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.shiro.generator.MainGenerator;
import com.shiro.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "generator", mixinStandardHelpOptions = true)
@Data
public class GeneratorCommand implements Callable<Integer> {

    /**
     * 是否生成循环
     */
    @Option(names = {"-l", "--loop"}, description = "是否生成循环", arity = "0..1", interactive = true, echo = true)
    private Boolean loop;

    /**
     * 作者注释
     */
    @Option(names = {"-a", "--author"}, description = "作者注释", arity = "0..1", interactive = true, echo = true)
    private String author = "shiro";

    /**
     * 输出信息
     */
    @Option(names = {"-o", "--output"}, description = "输出信息", arity = "0..1", interactive = true, echo = true)
    private String outputText = "Sum = ";

    @Override
    public Integer call() throws IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this, mainTemplateConfig);
        MainGenerator.doGenerator(mainTemplateConfig);
        return 0;
    }
}
