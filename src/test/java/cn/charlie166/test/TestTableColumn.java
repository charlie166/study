package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import cn.charlie166.spring.boot.web.domain.SqlServerColumnInfo;
import cn.charlie166.spring.boot.web.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库表字段测试类
 */
@Slf4j
//@ActiveProfiles(value = "test")
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = BootApplication.class)
public class TestTableColumn {

    @Test
    public void testLog() {
        log.debug("测试...");
    }

    @Test
    public void testGenerate() {
        String str = "jdbc:sqlserver://192.168.0.118:1433;DatabaseName=rzkjsoft_standard";
//        String str = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=rzkjsoft_standard";
        String username = "sa";
        String pwd = "sqlserver";
//        String pwd = "liyang@sql";
        String tableName = "RZBIZ_SYNC_TASK_MONITOR";
        try (Connection connection = DriverManager.getConnection(str, username, pwd)){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME = '" + tableName + "'");
            ResultSet rs = ps.executeQuery();
            List<SqlServerColumnInfo> list = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                list.add(SqlServerColumnInfo.of(rs.getString("COLUMN_NAME"), ConvertUtils.toInt(rs.getObject("ORDINAL_POSITION"), 0)));
            }
            list.sort(Comparator.comparingInt(SqlServerColumnInfo::getPosition));
            log.debug("查询结果: {}", list);
            if (CollectionUtils.isNotEmpty(list)) {
                String selectAlias = "t";
                /*全字段查询语句**/
                StringBuilder selectSql = new StringBuilder();
                selectSql.append("SELECT ");
                selectSql.append(list.stream().map(one -> selectAlias + "." + one.getColumn()).collect(Collectors.joining(", "))).append(System.lineSeparator());
                selectSql.append("FROM ").append(tableName).append(" ").append(selectAlias);
                log.debug("全字段查询语句:\r\n{}", selectSql);
                /*单条插入语句**/
                StringBuilder insertSql = new StringBuilder();
                insertSql.append("INSERT INTO ").append(tableName).append("(");
                insertSql.append(list.stream().map(SqlServerColumnInfo::getColumn).collect(Collectors.joining(", ")));
                insertSql.append(")").append(System.lineSeparator());
                insertSql.append("VALUES").append(System.lineSeparator());
                insertSql.append("(");
                insertSql.append(list.stream().map(one -> "#{" + one.getColumn() + "}").collect(Collectors.joining(", ")));
                insertSql.append(")").append(System.lineSeparator());
                log.debug("单条插入语句:\r\n{}", insertSql);
                /*批量插入语句**/
                StringBuilder batchInsertSql = new StringBuilder();
                batchInsertSql.append("INSERT INTO ").append(tableName).append("(");
                batchInsertSql.append(list.stream().map(SqlServerColumnInfo::getColumn).collect(Collectors.joining(", ")));
                batchInsertSql.append(")").append(System.lineSeparator());
                batchInsertSql.append("VALUES").append(System.lineSeparator());
                batchInsertSql.append("<foreach collection=\"data\" item=\"one\" separator=\",\">").append(System.lineSeparator());
                batchInsertSql.append("(");
                batchInsertSql.append(list.stream().map(one -> "#{one." + one.getColumn() + "}").collect(Collectors.joining(", ")));
                batchInsertSql.append(")").append(System.lineSeparator()).append("</foreach>").append(System.lineSeparator());
                log.debug("批量插入语句:\r\n{}", batchInsertSql);
                /*更新语句**/
                StringBuilder updateSql = new StringBuilder();
                updateSql.append("UPDATE ").append(tableName).append(System.lineSeparator()).append("<set>");
                updateSql.append(list.stream()
                        .filter(one -> !"ID".equalsIgnoreCase(one.getColumn()))
                        .map(one -> {
                    String s = System.lineSeparator() + "<if test=\"null != " + one.getColumn() + " and " + one.getColumn() + " != ''\">" + System.lineSeparator();
                    s += one.getColumn() + " = #{" + one.getColumn() + "}," + System.lineSeparator();
                    s += "</if>";
                    return s;
                }).collect(Collectors.joining(""))).append("</set>").append(System.lineSeparator());
                updateSql.append("<choose>").append(System.lineSeparator()).append("<when test=\"null != ids\">").append(System.lineSeparator())
                        .append("WHERE ID IN").append(System.lineSeparator())
                        .append("<foreach collection=\"ids\" item=\"one\" separator=\",\" open=\"(\" close=\")\">").append(System.lineSeparator())
                        .append("#{one}").append(System.lineSeparator())
                        .append("</foreach>").append(System.lineSeparator())
                        .append("</when>").append(System.lineSeparator())
                        .append("<otherwise>").append(System.lineSeparator())
                        .append("WHERE ID = #{ID}").append(System.lineSeparator())
                        .append("</otherwise>").append(System.lineSeparator())
                        .append("</choose>").append(System.lineSeparator());
                log.debug("更新语句:\r\n{}", updateSql);
            }
        } catch (SQLException e) {
            log.error("连接数据库失败", e);
        }
    }
}
