package cn.charlie166.study.web.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @ClassName: ClassUtils 
* @Description: 类操作通用工具类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年8月23日 
*
 */
public class ClassUtils {

	/**
	* @Title: getAllField 
	* @Description: 获取指定类所有变量，包含父类
	* @param cls 给定类
	* @return 所有变量域列表
	 */
	public static List<Field> getAllField(Class<?> cls){
		List<Field> fieldList = new ArrayList<Field>();
		if(cls != null){
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			if(cls.getSuperclass() != null){
				fieldList.addAll(ClassUtils.getAllField(cls.getSuperclass()));
			}
		}
		return fieldList;
	}
}	