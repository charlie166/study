package cn.charlie166.study.test;

import org.junit.After;
import org.junit.BeforeClass;

/**
* @ClassName: ParentTest 
* @Description: 单元测试父类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月13日 
*
 */
public class ParentTest {

	/**
	* @Title: beforeTest 
	* @Description: 单元测试前置公共操作
	 */
	@BeforeClass
	public static void beforeTest(){
		/**设置日志配置文件路径**/
		System.setProperty("log4j.configurationFile", "classpath:config/log4j2-test.xml");
	}
	
	/**
	* @Title: afterTest 
	* @Description: 单元测试后置公共操作
	 */
	@After
	public void afterTest(){
	}
}	