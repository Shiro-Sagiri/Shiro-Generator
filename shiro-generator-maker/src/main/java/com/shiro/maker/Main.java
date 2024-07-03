package com.shiro.maker;

import com.shiro.maker.generator.main.GenerateTemplate;
import freemarker.template.TemplateException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GenerateTemplate generator = new GenerateTemplate() {
        };
        try {
            generator.doGenerate();
        } catch (IOException | InterruptedException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
