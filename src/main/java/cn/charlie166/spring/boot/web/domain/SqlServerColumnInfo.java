package cn.charlie166.spring.boot.web.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SqlServer 数据库列信息
 */
@AllArgsConstructor(staticName = "of")
@Data
public class SqlServerColumnInfo {

    /*列名**/
    private String column;
    /*位置序号**/
    private int position;
}
