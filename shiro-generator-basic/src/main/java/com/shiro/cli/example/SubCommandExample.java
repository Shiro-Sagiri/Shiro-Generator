package com.shiro.cli.example;


import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "main", mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable {
    @Override
    public void run() {
        System.out.println("执行主命令");
    }

    @Command(name = "add", mixinStandardHelpOptions = true)
    static class AddCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("执行add命令");
        }
    }

    @Command(name = "delete", mixinStandardHelpOptions = true)
    static class DeleteCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("执行delete命令");
        }
    }

    @Command(name = "query", mixinStandardHelpOptions = true)
    static class QueryCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("执行query命令");
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SubCommandExample())
                .addSubcommand(new AddCommand())
                .addSubcommand(new DeleteCommand())
                .addSubcommand(new QueryCommand())
                .execute("update");
        System.exit(exitCode);
    }
}
