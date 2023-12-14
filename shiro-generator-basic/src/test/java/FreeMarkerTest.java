import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {

    @Test
    public void test() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        try {
            configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
            configuration.setNumberFormat("#");
            configuration.setDefaultEncoding("UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> dataModel = getDataModel();

        try {
            try (Writer out = new FileWriter("myweb.html")) {
                Template template = configuration.getTemplate("myweb.html.ftl");
                template.process(dataModel, out);
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> getDataModel() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2023);
        List<Map<String, Object>> menuItems = new ArrayList<>();
        Map<String, Object> menuItem1 = new HashMap<>();
        menuItem1.put("url", "https://www.baidu.com");
        menuItem1.put("label", "百度");
        Map<String, Object> menuItem2 = new HashMap<>();
        menuItem2.put("url", "https://www.huangliangzhong.cn/");
        menuItem2.put("label", "主页");
        menuItems.add(menuItem1);
        menuItems.add(menuItem2);
        dataModel.put("menuItems", menuItems);
        return dataModel;
    }
}
