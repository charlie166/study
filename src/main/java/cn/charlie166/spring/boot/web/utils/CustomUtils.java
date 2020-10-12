package cn.charlie166.spring.boot.web.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 工具类
 */
public class CustomUtils {

    private CustomUtils(){}

    private static final DateTimeFormatter f_19 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 返回当前时间字符串 {yyyy-MM-dd HH:mm:ss}
     * @return 时间字符串
     */
    public static String getCurrentTime() {
        return f_19.format(LocalDateTime.now());
    }

    /**
     * 获取当前方法名
     * @return 方法名
     */
    public static String getCurrentMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3) {
            return stackTrace[2].getMethodName();
        }
        return "";
    }
}
