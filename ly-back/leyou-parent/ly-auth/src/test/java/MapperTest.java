import com.leyou.LyAuthApplication;
import com.leyou.auth.mapper.ApplicationInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 创建时间：2020/12/26
 * 测试Mapper
 * @author wpf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LyAuthApplication.class)
public class MapperTest {

    @Autowired
    ApplicationInfoMapper mapper;

    @Test
    public void testSelectTargetAppNameListByServiceName() {
        List<String> stringList = mapper.selectTargetAppNameListByServiceName("api-gateway");
        stringList.forEach(System.out::println);
    }

}
