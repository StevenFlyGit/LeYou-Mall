import com.leyou.common.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 创建时间：2020/12/24
 * 测试生成RSA秘钥文件
 * @author wpf
 */

public class RsaTest {

    private String publicKeyPath = "E:\\实习&培训&工作\\黑马程序员培训\\就业班内容\\项目二阶段\\个人代码\\rsa-key.pub";
    private String privateKeyPath = "E:\\实习&培训&工作\\黑马程序员培训\\就业班内容\\项目二阶段\\个人代码\\rsa-key";

    @Test
    public void generateKey() throws Exception {
        /*
        参数1：公钥文件的文件名(包含路径)
        参数2：私钥文件的文件名(包含路径)
        参数3：加密使用的密文，无关紧要，可以随意填写
        参数4：生成密文大小，理论上说数值越大越安全，但同时需要加/解密的时间也越长，一般2048字节是比较合理的取值
         */
        RsaUtils.generateKey(publicKeyPath, privateKeyPath, "wpf", 2048);
    }

    @Test
    public void getPublicKey() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        //直接输出publicKey对象即可获取公钥内容
        System.out.println("publicKey = " + publicKey);
    }

    @Test
    public void getPrivateKey() throws Exception {
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        //直接输出privateKey对象即可获取私钥内容
        System.out.println("privateKey = " + privateKey);
    }

}
