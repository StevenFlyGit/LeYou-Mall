import com.leyou.LySearchApplication;
import com.leyou.search.service.GoodDataImportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 创建时间：2020/12/18
 * 商品搜索测试类
 * @author wpf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class goodsSearchTest {

    @Autowired
    GoodDataImportService goodDataImportService;

    @Test
    public void importGoodsData() {
        goodDataImportService.importGoodsMagFromSqlToEs();
    }

}
