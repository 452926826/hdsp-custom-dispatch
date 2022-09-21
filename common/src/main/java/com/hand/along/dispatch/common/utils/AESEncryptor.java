package com.hand.along.dispatch.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * AES加密工具类
 *
 * @author terry
 * @version 1.0
 * @date 2020/12/2 15:39
 */
public final class AESEncryptor {

    private static final Logger log = LoggerFactory.getLogger(AESEncryptor.class);

    private static final String KEY_ALGORITHM = "AES";
    /**
     * 默认的加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /**
     * 自定义密码
     */
    private static final String DEFAULT_SECURITY_KEY = "xxxx";

    private AESEncryptor() {

    }

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @return 返回Base64转码后的加密数据
     */
    @SuppressWarnings("WeakerAccess")
    public static String encrypt(String content) {
        if (Objects.isNull(content)) {
            return null;
        }
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //通过Base64转码返回
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            log.error("encrypt error", e);
        }
        return content;
    }

    /**
     * AES 解密操作
     *
     * @param content 待解密内容
     * @return 解密内容
     */
    @SuppressWarnings("WeakerAccess")
    public static String decrypt(String content) {
        if (Objects.isNull(content)) {
            return null;
        }
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());

            //执行操作
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decrypt error", e);
        }
        return content;
    }

    /**
     * 生成加密秘钥
     *
     * @return SecretKeySpec
     */
    private static SecretKeySpec getSecretKey() {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(DEFAULT_SECURITY_KEY.getBytes());
            //AES 要求密钥长度为 128
            kg.init(128, random);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("getSecretKey error", e);
        }
        return null;
    }

}
