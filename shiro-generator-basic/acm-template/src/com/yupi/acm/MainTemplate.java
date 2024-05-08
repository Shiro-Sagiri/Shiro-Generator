package com.yupi.acm;

import java.util.Scanner;

/**
 * ACM 输入模板（多数之和）
 * @author shiro
 */
public class MainTemplate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
FreeMarker template error (DEBUG mode; use RETHROW in production!):
The following has evaluated to null or missing:
==> loop  [in template "MainTemplate.java.ftl" at line 12, column 6]

----
Tip: If the failing expression is known to legally refer to something that's sometimes null or missing, either specify a default value like myOptionalVar!myDefault, or use <#if myOptionalVar??>when-present<#else>when-missing</#if>. (These only cover the last step of the expression; to cover the whole expression, use parenthesis: (myOptionalVar.foo)!myDefault, (myOptionalVar.foo)??
----

----
FTL stack trace ("~" means nesting-related):
	- Failed at: #if loop  [in template "MainTemplate.java.ftl" at line 12, column 1]
----

Java stack trace (for programmers):
----
freemarker.core.InvalidReferenceException: [... Exception message was already printed; see it above ...]
	at freemarker.core.InvalidReferenceException.getInstance(InvalidReferenceException.java:134)
	at freemarker.core.UnexpectedTypeException.newDescriptionBuilder(UnexpectedTypeException.java:85)
	at freemarker.core.UnexpectedTypeException.<init>(UnexpectedTypeException.java:48)
	at freemarker.core.NonBooleanException.<init>(NonBooleanException.java:47)
	at freemarker.core.Expression.modelToBoolean(Expression.java:195)
	at freemarker.core.Expression.evalToBoolean(Expression.java:178)
	at freemarker.core.Expression.evalToBoolean(Expression.java:163)
	at freemarker.core.ConditionalBlock.accept(ConditionalBlock.java:48)
	at freemarker.core.Environment.visit(Environment.java:335)
	at freemarker.core.Environment.visit(Environment.java:341)
	at freemarker.core.Environment.process(Environment.java:314)
	at freemarker.template.Template.process(Template.java:383)
	at com.shiro.generator.DynamicGenerator.doGenerator(DynamicGenerator.java:29)
	at com.shiro.generator.MainGenerator.doGenerator(MainGenerator.java:25)
	at com.shiro.cli.command.GenerateCommand.call(GenerateCommand.java:39)
	at com.shiro.cli.command.GenerateCommand.call(GenerateCommand.java:13)
	at picocli.CommandLine.executeUserObject(CommandLine.java:2041)
	at picocli.CommandLine.access$1500(CommandLine.java:148)
	at picocli.CommandLine$RunLast.executeUserObjectOfLastSubcommandWithSameParent(CommandLine.java:2461)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:2453)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:2415)
	at picocli.CommandLine$AbstractParseResultHandler.execute(CommandLine.java:2273)
	at picocli.CommandLine$RunLast.execute(CommandLine.java:2417)
	at picocli.CommandLine.execute(CommandLine.java:2170)
	at com.shiro.cli.CommandExecutor.doExecute(CommandExecutor.java:33)
	at com.shiro.Main.main(Main.java:9)
