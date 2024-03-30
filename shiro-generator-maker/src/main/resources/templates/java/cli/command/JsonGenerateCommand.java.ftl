package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;

@Data
@CommandLine.Command(name = "json-generator", description = "根据json文件生成代码", mixinStandardHelpOptions = true)
public class JsonGeneratorCommand implements Runnable {

    @CommandLine.Option(names = {"--filePath"}, arity = "0..1", description = "json文件路径", interactive = true, echo = true)
    private String filePath;

    @Override
    public void run() {
        String jsonStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(jsonStr, DataModel.class);
        try {
            MainGenerator.doGenerate(dataModel);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
