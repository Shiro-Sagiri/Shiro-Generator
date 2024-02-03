package com.shiro.maker;

import com.shiro.maker.generator.MainGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        MainGenerator mainGenerator = new MainGenerator();
        try {
            mainGenerator.doGenerate();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
