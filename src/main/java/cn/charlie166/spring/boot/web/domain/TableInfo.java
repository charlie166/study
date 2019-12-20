package cn.charlie166.spring.boot.web.domain;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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
    private TableInfoIndex primary;
    /*索引**/
    private List<TableInfoIndex> index;

    @Override
    public String toString() {
        return name + ":{" + (CollectionUtils.isNotEmpty(fields) ? fields.stream().map(TableInfoField::getField).collect(Collectors.joining(",")) : "") + "]";
    }
}
