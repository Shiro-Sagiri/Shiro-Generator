package com.shiro.maker;

import com.shiro.maker.generator.main.GenerateTemplate;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GenerateTemplate generator = new GenerateTemplate() {
        };
        try {
            generator.doGenerate();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
