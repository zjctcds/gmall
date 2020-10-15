package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GmallAuthApplicationTests {

    // 别忘了创建D:\\project\rsa目录
    private static final String pubKeyPath = "D:\\guli\\rsa\\rsa.pub";
    private static final String priKeyPath = "D:\\guli\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "jklZXC123,./");
    }

    @BeforeEach
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 1);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE2MDI2NjQ5ODN9.RHuiksBhz6vbhsuf-BoKOhBln-4cMUfszcI_64FYxTwQ4mPRtBUsqdJzDiH8uG_LQERXwQAuf5hZag8E_2Cx0_cl8Boe0n1vzX05x2o-Z8WyAlLUgxUbOLLmnLT5TKZPpVIDMvzlFmnBZoklzK_UQnNL30j9O3xDuCFE_nUoM9USATJhbdgNhyGFJUVSNXQ0fSlu4wtSgz_WfurpOPLjKsUBu4XHJMROvqXqqYhSPBnrv7HtWK-Fgy7SCd0KIe96wQ2gMrRA_20sWG6PYKc8vf4Kuem1vjpTkuanUtNEC4yr_RymhOut3By0qWKU5t-ekZw9LUgDDL9auYON9mcJ7w";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}
