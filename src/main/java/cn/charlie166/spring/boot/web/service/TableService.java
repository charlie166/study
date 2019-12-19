package cn.charlie166.spring.boot.web.service;

/**
 * 数据库表服务接口
 */
public interface TableService {

    /**
     * 从文件中解析脚本语句
     * @param filePath 来源文件
     * @return 脚本语句
     */
    String parseScriptFrom(String filePath);
}
