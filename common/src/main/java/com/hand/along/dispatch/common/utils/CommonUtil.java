package com.hand.along.dispatch.common.utils;

import com.hand.along.dispatch.common.exceptions.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtil {
    private static final Pattern linePattern = Pattern.compile("_(\\w)");
    private static final Pattern humpPattern = Pattern.compile("[A-Z]");
    public static final String defaultPattern = "yyyy-MM-dd HH:mm:ss";

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static String formatNow() {
        return getDf(defaultPattern).format(now());
    }

    public static String formatNow(String pattern) {
        return getDf(pattern).format(now());
    }

    public static String formatDate(String pattern, Date date) {
        return getDf(pattern).format(date);
    }

    public static String formatDate(Date date) {
        return getDf(defaultPattern).format(date);
    }

    public static FastDateFormat getDf(String pattern) {
        return FastDateFormat.getInstance(pattern);
    }

    public static Date format(String pattern, String date) {
        try {
            return FastDateFormat.getInstance(pattern).parse(date);
        } catch (ParseException e) {
            log.error("时间格式化错误", e);
            throw new CommonException("时间格式化错误");
        }
    }

    public static String getMac() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface
                    .getNetworkInterfaces();
            List<String> localMacs = new ArrayList<>();
            for (NetworkInterface networkInterface : Collections.list(nets)) {
                byte[] mac = networkInterface.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                if (mac != null) {
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i],
                                (i < mac.length - 1) ? "-" : ""));
                    }
                }
                localMacs.add(sb.toString().toUpperCase());
            }
            return localMacs.get(0);
        } catch (Exception e) {
            log.error("获取Mac地址失败", e);
        }
        String uuid = UUID.randomUUID().toString();
        log.warn("mac地址获取失败，使用uuid代替，uuid的缺陷是每次重启后都会重新选举一个master,当前uuid:{}", uuid);
        return uuid;
    }


    public static String getIp(String pattern) {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    // IPV4
                    if (inetAddress instanceof Inet4Address) {
                        String ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                        if (StringUtils.isNotEmpty(pattern)) {
                            log.info("指定了master的IP前缀：{}", pattern);
                            if (ip.startsWith(pattern)) {
                                return ip;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error("ip解析错误！", e);
        }
        return ipList.get(0);
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 驼峰转下划线
     *
     * @param str str
     * @return str
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        return result.startsWith("_") ? result.substring(1) : result;
    }

}
