package com.shiro;

import com.shiro.cli.CommandExecutor;

public class Main {
    public static void main(String[] args) {
//        args = new String[]{"generator", "-l", "-a", "-o"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}
