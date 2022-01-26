package com.hand.along.dispatch.common.utils;

import com.hand.along.dispatch.common.exceptions.CommonException;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述： 匹配变量
 *
 * @author zhilong.deng@hand-china.com
 * @version 1.0.0
 * @date 2022/1/15 16:29
 */
@Slf4j
public class VariableUtil {
    private static final Pattern VARIABLE_REGEX = Pattern.compile("\\$\\{(.*?)}");
    public static final String VARIABLE_FORMAT = "${%s}";
    public static final ScriptEngineManager manager = new ScriptEngineManager();
    public static final ScriptEngine se = manager.getEngineByName("js");

    /**
     * 匹配参数列表
     *
     * @param context 匹配内容
     * @return 返回匹配的结果
     */
    public static Set<String> variableList(String context) {
        Set<String> result = new HashSet<>();
        final Matcher matcher = VARIABLE_REGEX.matcher(context);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    /**
     * 替换参数
     *
     * @param context 待替换的字符串
     * @param params  参数
     */
    public static String replaceVariable(String context, Map<String, Object> params) {
        Set<String> paramKeys = variableList(context);
        for (String paramKey : paramKeys) {
            log.info("替换参数：{}", paramKey);
            if (params.containsKey(paramKey)) {
                context = context.replace(String.format(VARIABLE_FORMAT, paramKey), params.get(paramKey).toString());
            }
        }
        return context;
    }

    /**
     * 计算表达式
     *
     * @param condition 计算条件
     * @return 返回true/false
     */
    public static boolean eval(String condition) {
        try {
            log.info("表达式计算: [{}]", condition);
            return Boolean.parseBoolean(se.eval(condition).toString());
        } catch (ScriptException e) {
            log.error("表达式计算错误: [{}]", condition, e);
            throw new CommonException("表达式计算错误", e);
        }
    }
    /**
     * 计算表达式
     *
     * @param condition 计算条件
     * @return 值
     */
    public static String evalValue(String condition) {
        try {
            log.info("表达式计算: [{}]", condition);
            return se.eval(condition).toString();
        } catch (ScriptException e) {
            log.error("表达式计算错误: [{}]", condition, e);
            throw new CommonException("表达式计算错误", e);
        }
    }

}
