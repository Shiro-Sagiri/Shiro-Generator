package com.shiro.maker;

import com.shiro.maker.generator.main.ZipGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ZipGenerator generator = new ZipGenerator();
        try {
            generator.doGenerate();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
