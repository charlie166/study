package cn.charlie166.spring.boot.web.utils;

/**
 * 转换工具类
 */
public class ConvertUtils {

    private ConvertUtils(){}

    /**
     * 将对象转换为整型
     * @param obj 转换来源
     * @param defaultVal 默认值
     * @return 结果
     */
    public static int toInt(Object obj, int defaultVal) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if(obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt(obj.toString());
        }
        return defaultVal;
    }
}
