package cn.charlie166.spring.boot.web.domain;

import lombok.Data;

/**
 * 数据库表中的字段信息
 */
@Data
public class TableInfoField {

    /*字段名称**/
    private String field;
    /*中文名**/
    private String chinese;
    /*字段类型**/
    private String type;
    /*默认值**/
    private String defaultVal;
    /*是否不为空**/
    private boolean notNull = true;
    /*备注**/
    private String remark;
}
