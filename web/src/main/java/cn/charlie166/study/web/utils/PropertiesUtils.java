package cn.charlie166.study.web.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
* @ClassName: PropertiesUtils 
* @Description: 属性操作工具类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月9日 
*
 */
public class PropertiesUtils {

	/**保存的属性键值对**/
	private static Map<String, String> map = new HashMap<String, String>();
	
	static {
		PropertiesUtils.loadProperties();
	}
	
	/**
	* @Title: loadProperties 
	* @Description: 加载属性文件
	 */
	private static void loadProperties(){
		Properties properties = new Properties();
		String files[] = {"/config/common.properties", "/config/dao.properties"};
		Arrays.asList(files).forEach(str -> {
			try {
				properties.load(PropertiesUtils.class.getResourceAsStream(str));
				properties.forEach((k, v) -> {
					if(k != null && v != null){
						PropertiesUtils.map.put(k.toString(), v.toString());
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	* @Title: getVal 
	* @Description: 获取配置的值
	* @param key 查询键
	* @return 配置的值
	 */
	public static String getVal(String key){
		if(StringUtils.hasContent(key)){
			return PropertiesUtils.map.get(key);
		}
		return null;
	}
	
	public static void main(String[] args) {
		List<Path> list = FileUtils.getFileWithSuffix("F:/charlie/", "zip");
		System.out.println("size:" + list.size());
		System.out.println("val:" + PropertiesUtils.getVal("project.charset"));
	}
}