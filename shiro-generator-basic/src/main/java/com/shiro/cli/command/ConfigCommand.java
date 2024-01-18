package com.shiro.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.shiro.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

@CommandLine.Command(name = "config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);
        for (Field field : fields) {
            System.out.println("参数类型" + field.getType());
            System.out.println("参数名称" + field.getName());
        }
    }
}
