package cn.charlie166.study.web.utils;


/**
* @ClassName: StringUtils 
* @Description: 字符串工具类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2016年12月26日 
*
 */
public class StringUtils {

	/**空白字符串***/
	public final static String EMPTY_STRING = "";
	
	/**
	* @Title: isNullOrEmpty 
	* @Description: 判断给定字符串是否为null或空白字符串
	* @param str 判断字符串
	* @return
	 */
	public static boolean isNullOrEmpty(String str){
		return str == null || StringUtils.EMPTY_STRING.equals(str.trim());
	}
	
	/**
	* @Title: hasContent 
	* @Description: 判断字符串是否有内容
	* @param str 判定字符串
	* @return
	 */
	public static boolean hasContent(String str){
		return !StringUtils.isNullOrEmpty(str);
	}
	
	/**
	* @Title: removeBlank 
	* @Description: 处理掉给定字符串中的空白字符
	* @param str 被操作的字符串
	* @return
	 */
	public static String replaceBlankWith(String str){
		return StringUtils.replaceBlankWith(str, StringUtils.EMPTY_STRING);
	}
	
	/**
	* @Title: replaceBlankWith 
	* @Description: 替换给定字符串中的空白内容
	* @param str 操作字符串
	* @param rep 替换内容
	* @return
	 */
	public static String replaceBlankWith(String str, String rep){
		if(StringUtils.hasContent(str)){
			return str.replaceAll("\\s+", rep);
		} else {
			return StringUtils.EMPTY_STRING;
		}
	}
	
	/**
	* @Title: toInteger 
	* @Description: 转换对象为整型封装类型
	* @param obj 需要转换的数据
	* @return 转换结果，无法转换时为null
	 */
	public static Integer toInteger(Object obj){
		return obj != null ? StringUtils.toInteger(obj.toString()) : null;
	}
	
	/**
	* @Title: toInteger 
	* @Description: 将字符串转换为整数封装类型
	* @param str 需要转换的字符串
	* @return 转换结果，无法转换时返回null
	 */
	public static Integer toInteger(String str){
		return StringUtils.toInteger(str, null);
	}
	
	/**
	* @Title: toInteger 
	* @Description: 将字符串转换为整数封装类型
	* @param str 需要转换的字符串
	* @param defaultVal 无法正确转换时的默认值
	* @return 转换结果
	 */
	public static Integer toInteger(String str, Integer defaultVal){
		if(StringUtils.hasContent(str)){
			try {
				return Integer.valueOf(str);
			} catch (Exception e) {
			}
		}
		return defaultVal;
	}
	
	public static void main(String[] args) {
		String str = " de rf          rw fve ";
		System.out.println("result:" + StringUtils.replaceBlankWith(str));
	}
}
