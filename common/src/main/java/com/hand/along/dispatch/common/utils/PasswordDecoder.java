package com.hand.along.dispatch.common.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 密码解析类
 *
 */
public final class PasswordDecoder {

    private static final Pattern PASSWORD_REGEX = Pattern.compile("\\$\\{_pwd:(.*?)}");

    private static final String PASSWORD_FORMAT = "${_pwd:%s}";


    private PasswordDecoder() {
        throw new UnsupportedOperationException("Util class can not instantiate");
    }

    /**
     * 获取密码解密后的内容
     *
     * @param jobContent 内容
     * @return 解密后的内容
     */
    public static String decodePassword(String jobContent) {
        Matcher matcher = PASSWORD_REGEX.matcher(jobContent);
        while (matcher.find()) {
            String passwordEncrypted = matcher.group(1);
            String passwordDecrypted = AESEncryptor.decrypt(passwordEncrypted);
            if(Objects.nonNull(passwordDecrypted)) {
                jobContent = jobContent.replace(String.format(PASSWORD_FORMAT, passwordEncrypted), passwordDecrypted);
            }
        }
        return jobContent;
    }
}
