import com.leyou.LyUserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 创建时间：2020/12/23
 * 测试利用BCrypt加密(使用Spring-Security中的BCrypt算法)
 * @author wpf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LyUserApplication.class)
public class SpringSecurityBCryptDemo {

    private String code1 = "$2a$10$ZJ20POu4nlVSsNEq3HZdZufCIpG8/jNVro0oY7rFvlv1zKSEcIH1S";
    private String code2 = "$2a$10$OHlOq5G2ASx.E3AmA85VceWhrIV24osdBM3I7/ckM.ftfhMXKmFIC";

    /**
     * $2a$10$ZJ20POu4nlVSsNEq3HZdZufCIpG8/jNVro0oY7rFvlv1zKSEcIH1S
     * $2a$10$OHlOq5G2ASx.E3AmA85VceWhrIV24osdBM3I7/ckM.ftfhMXKmFIC
     * 测试加密
     * 动态加盐加密：加盐规则是动态的，每次加密结构都不一样。
     */
    @Test
    public void encodeTest() {
        //加密方式1：使用BCryptPasswordEncoder对象(自动生成随机盐)
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode1 = passwordEncoder.encode("123456");
        System.out.println("方式1encode = " + encode1);

        //加密方式2：使用BCrypt类的静态方法(手动生成随机盐)
        String salt = BCrypt.gensalt();
        String encode2 = BCrypt.hashpw("123456", salt);
        System.out.println("方式2encode = " + encode2);

        //加密方式3，不需要使用到spring-security，直接使用jBCrypt相关依赖下的BCrypt类加密

    }

    @Test
    public void decodeTest() {
        //解密方式1：使用BCryptPasswordEncoder对象
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean decodeResult1 = passwordEncoder.matches("123456", code2);
        System.out.println("decodeResult1 = " + decodeResult1);

        //解密方式2：使用BCrypt类的静态方法
        boolean decodeResult2 = BCrypt.checkpw("123456", code1);
        System.out.println("decodeResult2 = " + decodeResult2);
    }

}
