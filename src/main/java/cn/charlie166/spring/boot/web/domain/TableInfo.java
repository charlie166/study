package cn.charlie166.spring.boot.web.domain;

import lombok.Data;

import java.util.List;

/**
 * 数据库表单信息对象
 */
@Data
public class TableInfo {

    /*表名,英文的**/
    private String name;
    /*中文表名描述**/
    private String label;
    /*表字段列表**/
    private List<TableInfoField> fields;
    /*主键**/
    private TableInfoPrimary primary;
    /*索引**/
    private TableInfoIndex index;
}
