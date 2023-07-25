import com.leyou.LyUserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 创建时间：2020/12/23
 * 使用BCrypt算法原生的jBCrypt依赖
 * @author wpf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LyUserApplication.class)
public class jBCryptBCryptDemo {

    private String code = "$2a$10$KsL2YKMXHV0CdOtLWcAEc.LQxEV6E/Ons1xeMW5Bp2BXXt/gxIeBC";

    @Test
    public void encodeTest() {
        //加密方式3，不需要使用到spring-security，直接使用jBCrypt相关依赖下的BCrypt类加密
        //但与spring-security下的BCrypt类使用方法相同
        String salt = BCrypt.gensalt();
        String code = BCrypt.hashpw("123456", salt);
        System.out.println("code = " + code);
    }

    @Test
    public void decodeTest() {
        //解密方式3，不需要使用到spring-security，直接使用jBCrypt相关依赖下的BCrypt类解密
        //但与spring-security下的BCrypt类使用方法相同
        boolean decodeResult = BCrypt.checkpw("123456", code);
        System.out.println("decodeResult = " + decodeResult);

    }
}
