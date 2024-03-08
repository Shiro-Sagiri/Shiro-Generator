# Shiro Generator

> 代码生成器项目

## 2023/12/13

    完成静态文件生成的两种方式
    1. 使用hutool工具类 2. 使用递归方式

## 2023/12/14

    使用FreeMarker模板引擎生成代码
    动静结合生成模板文件

## 2024/01/06

    学习picocli命令行框架的基本使用

## 2024/01/16

    完成命令行代码生成器(第一阶段)

## 2024/02/07

    增强制作器的能力,增强配置能力

## 2024/02/20
    完成模板制作工具

## 2024/03/06
    DEBUG: 修改模板制作工具文件过滤,排除修改或添加制作模板时,会把已经生成的模板文件当成静态文件生成
    DEBUG: 修改模板生成逻辑,修复已经生成模板文件但内容未变动,会导致meta文件中文件信息中的ftl动态模板文件信息变为静态

## 2024/03/07
    DEBUG: 修复制作工具生成meta配置文件的时候,文件信息中的输入和输出路径相反
    DEBUG: 修复meta文件也会当成项目文件被扫描

## 2024/03/08
    使用模板制作工具制作了一个springboot-init模板项目
    TODO: 生成的模板包名修改但包名对应的文件夹名称没修改