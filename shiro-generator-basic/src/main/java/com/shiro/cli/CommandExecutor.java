package com.shiro.cli;


import com.shiro.cli.command.ConfigCommand;
import com.shiro.cli.command.GeneratorCommand;
import com.shiro.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "shiro", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GeneratorCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        System.out.println("请输入具体命令,或输入 --help 查看命令提示");
    }

    /**
     * 执行命令
     * @param args 命令参数
     * @return 执行结果
     */
    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }
}
