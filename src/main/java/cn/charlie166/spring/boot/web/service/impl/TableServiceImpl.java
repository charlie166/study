package cn.charlie166.spring.boot.web.service.impl;

import cn.charlie166.spring.boot.web.domain.TableInfo;
import cn.charlie166.spring.boot.web.exception.CustomException;
import cn.charlie166.spring.boot.web.service.TableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 建表服务实现类
 */
@Slf4j
@Service
public class TableServiceImpl implements TableService {

    /*匹配数据库表名**/
    private Pattern TABLE_NAME_PATTERN = Pattern.compile("[(（]([\\w\\u4e00-\\u9fa5]+?)[)）]");

    @Override
    public String parseScriptFrom(String filePath) {
        Objects.requireNonNull(filePath, "来源文件不能为空");
        Path path = Paths.get(filePath);
        if(path == null || !Files.exists(path))
            throw CustomException.t("文件不存在");
        if(!Files.isRegularFile(path) || !Files.isReadable(path))
            throw CustomException.t("不是文件或者文件不可读");
        Path fileName = path.getFileName();
        log.debug("处理文件:{}", fileName);
        if(fileName.toString().toLowerCase().endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(path))){
                Iterator<XWPFTable> tablesIterator = doc.getTablesIterator();
                while (tablesIterator.hasNext()) {
                    XWPFTable table = tablesIterator.next();
                    String[] checkResult = this.checkDatabaseTable(table);
                    if(checkResult != null) {
                        if(checkResult[0].matches("[a-zA-z0-9_]+")){
                            log.debug("数据库表: {}----{}", checkResult[0], checkResult[1]);
                        } else {
                            log.debug("存在其他字符表名: {}--{}", checkResult[0], checkResult[1]);
                        }
                    }
                }
            } catch (IOException ioe) {
                log.error("处理出现错误", ioe);
            }
        } else {
            log.error("未实现");
        }
        return null;
    }

    /***
     * 判断是否为数据库表设计的表格
     * @param table 判断对象
     * @return 对应表名 [0]: 表名; [1]: 中文表名描述. 可能不存在.返回空字符串
     */
    private String[] checkDatabaseTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        if(CollectionUtils.isNotEmpty(rows)) {
            XWPFTableRow firstRow = rows.get(0);
            List<XWPFTableCell> cells = firstRow.getTableCells();
            /*目前只有第一行只有一个单元格的判定为数据库表设计**/
            if(CollectionUtils.isNotEmpty(cells) && cells.size() == 1) {
                XWPFTableCell cell = cells.get(0);
                String content = cell.getText();
                if(content.contains("表")) {
                    int index = content.contains(":") ? content.indexOf(":") : content.indexOf("：");
                    String s = content.substring(index + 1).trim();
                    Matcher matcher = TABLE_NAME_PATTERN.matcher(s);
                    if(matcher.find()){
                        String [] tmp = new String[]{"", matcher.group(1)};
                        tmp[0] = s.substring(0, matcher.start());
                        return tmp;
                    } else {
                        return new String[]{s, ""};
                    }
                }
            }
        }
        return null;
    }

    /***
     * 将表格数据内容转换为数据表对象
     * @param table 表格数据
     * @return 数据表信息
     */
    private TableInfo convert(XWPFTable table) {
        Objects.requireNonNull(table, "数据表不能为空");
        return null;
    }
}
