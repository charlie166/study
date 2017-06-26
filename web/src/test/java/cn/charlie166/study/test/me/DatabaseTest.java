package cn.charlie166.study.test.me;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.charlie166.study.test.ParentTest;
import cn.charlie166.study.web.utils.DatabaseUtils;

/**
* @ClassName: DatabaseTest 
* @Description: 数据库测试类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月14日 
*
 */
public class DatabaseTest extends ParentTest{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/***
	* @Title: testInsert 
	* @Description: 测试新增数据
	 */
	@Test
	public void testInsert(){
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO demo(name, title) VALUES('a0', 't0')");
		try {
			PreparedStatement ps = DatabaseUtils.getConnection().prepareStatement(sql.toString());
			int result = ps.executeUpdate();
			logger.debug("执行结果:" + result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}	