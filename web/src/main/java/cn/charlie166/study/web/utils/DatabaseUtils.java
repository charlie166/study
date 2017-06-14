package cn.charlie166.study.web.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
* @ClassName: DatabaseUtils 
* @Description: 数据库操作工具类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月13日 
*
 */
public class DatabaseUtils {

	private final static String driver = PropertiesUtils.getVal("db.driver");
	private final static String url = PropertiesUtils.getVal("db.url");
	private final static String user = PropertiesUtils.getVal("db.username");
	private final static String pwd = PropertiesUtils.getVal("db.password");
	
	static {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* @Title: getConnection 
	* @Description: 获取数据库连接
	* @return
	 */
	public static Connection getConnection(){
		try {
			return DriverManager.getConnection(url, user, pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}