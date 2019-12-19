package cn.charlie166.spring.boot.web.domain;

import lombok.Data;

import java.util.Set;

/**
 * 数据表主键信息
 */
@Data
public class TableInfoPrimary {

    /*主键名称**/
    private String name;
    /*对应字段集合**/
    private Set<String> field;
}
