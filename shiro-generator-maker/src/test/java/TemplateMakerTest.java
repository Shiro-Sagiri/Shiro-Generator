import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.shiro.maker.template.TemplateMaker;
import com.shiro.maker.template.model.TemplateMakerConfig;
import org.junit.Test;

public class TemplateMakerTest {

    @Test
    public void makeSpringBootTemplate() {
        String rootPath = "example/springboot-init/";
        String jsonStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        //todo: 修改包名之后还需改变文件夹的命名
//        String jsonStr1 = ResourceUtil.readUtf8Str(rootPath + "templateMaker1.json");
//        String jsonStr2 = ResourceUtil.readUtf8Str(rootPath + "templateMaker2.json");
//        String jsonStr3 = ResourceUtil.readUtf8Str(rootPath + "templateMaker3.json");
//        String jsonStr4 = ResourceUtil.readUtf8Str(rootPath + "templateMaker4.json");
//        String jsonStr5 = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
//        String jsonStr6 = ResourceUtil.readUtf8Str(rootPath + "templateMaker6.json");
//        String jsonStr7 = ResourceUtil.readUtf8Str(rootPath + "templateMaker7.json");
//        String jsonStr8 = ResourceUtil.readUtf8Str(rootPath + "templateMaker8.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(jsonStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }
}
