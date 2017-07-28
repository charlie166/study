package cn.charlie166.study.test.me;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.charlie166.study.test.ParentTest;
import cn.charlie166.study.web.utils.FileUtils;

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
	
	@Test
	public void testListenDirection(){
		logger.debug("测试监听文件夹...");
		FileUtils.listenDirectionCreate(Paths.get("F:/rato/test"));
	}
	
	@Test
	public void testIterator(){
		logger.debug("测试遍历文件夹...");
		List<Path> files = FileUtils.getPathWithSuffix("F:/rato/test", null);
		logger.debug("匹配文件数:" + files.size());
		logger.debug("文件:[" + files.stream().map(one -> one.toString()).collect(Collectors.joining(";")) + "]");
	}
	
}	