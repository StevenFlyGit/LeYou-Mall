import com.leyou.LyGatewayApplication;
import com.leyou.client.auth.AuthClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 创建时间：2020/12/26
 * auth微服务的Feign接口测试
 * @author wpf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LyGatewayApplication.class)
public class AuthClientTest {

    @Autowired
    private AuthClient authClient;

    @Test
    public void testApplicationAuthorize() {
        String token = authClient.applicationAuthorize("api-gateway", "api-gateway");
        System.out.println("token = " + token);
    }

}
