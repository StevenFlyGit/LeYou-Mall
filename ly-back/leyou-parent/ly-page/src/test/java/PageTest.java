import com.leyou.LyPageApplication;
import com.leyou.page.service.StaticPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 创建时间：2020/12/22
 * 商品详情静态页相关方法单元测试类
 * @author wpf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LyPageApplication.class)
public class PageTest {

    @Autowired
    StaticPageService staticPageService;

    @Test
    public void createStaticPageTest() {
        staticPageService.createStaticPage(88L);
    }

}
