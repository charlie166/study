package cn.charlie166.spring.boot.web.domain;

import lombok.Data;

import java.util.Set;

/**
 * 数据表索引信息
 */
@Data
public class TableInfoIndex {

    /*索引名称**/
    private String name;
    /*索引字段集合**/
    private Set<String> field;
}
