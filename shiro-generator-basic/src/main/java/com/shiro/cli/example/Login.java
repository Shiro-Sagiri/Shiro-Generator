package com.shiro.cli.example;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, description = "Passphrase", interactive = true, prompt = "Enter password: ", echo = true, arity = "0..1")
    String password;

    @Option(names = {"-cp", "--checkPassword"}, description = "check password", interactive = true, arity = "0..1")
    String checkPassword;

    public Integer call() {
        System.out.println("User: " + user);
        System.out.println("Password: " + password);
        System.out.println("CheckPassword: " + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Login()).execute(args);
        System.exit(exitCode);
    }
}