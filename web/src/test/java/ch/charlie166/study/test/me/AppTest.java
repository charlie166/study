package ch.charlie166.study.test.me;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.charlie166.study.test.ParentTest;

/**
* @ClassName: AppTest 
* @Description: 测试用例
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月9日 
*
 */
public class AppTest extends ParentTest{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void testLogger(){
		logger.debug("测试输出...");
	}
	
}	