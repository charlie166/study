package cn.charlie166.spring.boot.web.service.impl;

import cn.charlie166.spring.boot.web.domain.*;
import cn.charlie166.spring.boot.web.exception.CustomException;
import cn.charlie166.spring.boot.web.service.TableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 建表服务实现类
 */
@Slf4j
@Service
public class TableServiceImpl implements TableService {

    /*匹配数据库表名**/
    private Pattern PATTERN_TABLE_NAME = Pattern.compile("[(（]([\\w\\u4e00-\\u9fa5]+?)[)）]");
    /*数字正则表达式**/
    private Pattern PATTERN_NUMBER = Pattern.compile("\\d+");

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
                            TableInfo tableInfo = this.convert(table);
                            if(tableInfo != null) {
                            } else {
                                log.debug("数据表[{}]无法正确处理", checkResult[0]);
                            }
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
                    Matcher matcher = PATTERN_TABLE_NAME.matcher(s);
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
        String[] names = this.checkDatabaseTable(table);
        /*这里只处理满足数据库设计表格式的表格**/
        if(names != null) {
            TableInfo ti = new TableInfo();
            ti.setName(names[0]);
            ti.setLabel(names[1]);
            List<XWPFTableRow> rows = table.getRows();
            /*字段配置开始索引**/
            int startIndex = -1;
            /*字段配置结束索引**/
            int endIndex = -1;
            /*属性对应下标[序号, 字段名称, 中文名称, 数据类型, 默认值, 是否为空, 备注]**/
            int []indexInfo = new int[7];
            Arrays.fill(indexInfo, -1);
            /*判断字段设置下标及位置信息**/
            for (int i = 0; i < rows.size(); i++) {
                XWPFTableRow row = rows.get(i);
                if(row != null && row.getTableCells() != null) {
                    List<XWPFTableCell> tableCells = row.getTableCells().stream().filter(Objects::nonNull).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(tableCells) && tableCells.get(0) != null) {
                        /*这样子写的话就是以第一个单元格来判断**/
                        XWPFTableCell firstCell = tableCells.get(0);
                        if(firstCell.getText().trim().equals("序号")) {
                            /*抬头下一行为字段配置开始行**/
                            startIndex = i + 1;
                            /*判断对应属性下标**/
                            indexInfo[0] = this.containCheckIndex(tableCells, "序号");
                            indexInfo[1] = this.containCheckIndex(tableCells, "字段");
                            indexInfo[2] = this.containCheckIndex(tableCells, "中文");
                            indexInfo[3] = this.containCheckIndex(tableCells, "类型");
                            indexInfo[4] = this.containCheckIndex(tableCells, "默认");
                            indexInfo[5] = this.containCheckIndex(tableCells, "空");
                            indexInfo[6] = this.containCheckIndex(tableCells, "备注");
                        }
                        /*第一个单元格不包含数字的---- TODO getText()方法获取不到编号**/
                        if(startIndex > -1 && !PATTERN_NUMBER.matcher(firstCell.getText().trim()).find()) {
                            /*前面一行为字段配置最后一行**/
                            if (i > startIndex) {
                                endIndex = i - 1;
                            }
                        }
                    }
                }
            }
            if (startIndex >= 0 && endIndex >= startIndex) {
                List<TableInfoField> fieldList = new ArrayList<>(endIndex - startIndex + 1);
                for (int i = startIndex; i <= endIndex; i ++) {
                    XWPFTableRow row = rows.get(i);
                    TableInfoField field = new TableInfoField();
                    if(indexInfo[0] >= 0 && row.getTableCells().size() > indexInfo[0] && StringUtils.hasText(row.getCell(indexInfo[0]).getText())) {
                        /*序号这一列需要从内容提取**/
                        Matcher matcher = PATTERN_NUMBER.matcher(row.getCell(indexInfo[0]).getText());
                        if(matcher.find()) {
                            field.setSort(Integer.parseInt(matcher.group()));
                        }
                    }
                    if(indexInfo[1] >= 0 && row.getTableCells().size() > indexInfo[1] && StringUtils.hasText(row.getCell(indexInfo[1]).getText())) {
                        field.setField(row.getCell(indexInfo[1]).getText().trim());
                    }
                    if(indexInfo[2] >= 0 && row.getTableCells().size() > indexInfo[2] && StringUtils.hasText(row.getCell(indexInfo[2]).getText())) {
                        field.setChinese(row.getCell(indexInfo[2]).getText());
                    }
                    if(indexInfo[3] >= 0 && row.getTableCells().size() > indexInfo[3] && StringUtils.hasText(row.getCell(indexInfo[3]).getText())) {
                        field.setType(row.getCell(indexInfo[3]).getText().trim());
                    }
                    if(indexInfo[4] >= 0 && row.getTableCells().size() > indexInfo[4] && StringUtils.hasText(row.getCell(indexInfo[4]).getText())) {
                        field.setDefaultVal(row.getCell(indexInfo[4]).getText().trim());
                    }
                    if(indexInfo[5] >= 0 && row.getTableCells().size() > indexInfo[5] && StringUtils.hasText(row.getCell(indexInfo[5]).getText())) {
                        field.setNotNull(row.getCell(indexInfo[5]).getText().replaceAll("\\S+", "").toLowerCase().equals("notnull"));
                    }
                    if(indexInfo[6] >= 0 && row.getTableCells().size() > indexInfo[6] && StringUtils.hasText(row.getCell(indexInfo[6]).getText())) {
                        field.setRemark(row.getCell(indexInfo[6]).getText());
                    }
                    fieldList.add(field);
                }
                ti.setFields(fieldList);
            }
            /*主键设置**/
            List<XWPFTableRow> subList = rows.subList(endIndex, rows.size());
            subList.stream().filter(row -> row.getTableCells().stream()
                .anyMatch(cell -> cell != null && StringUtils.hasText(cell.getText()) && cell.getText().contains("主键"))).findAny()
                .ifPresent(row -> {
                    /*至少需要2个单元格内容**/
                    if(CollectionUtils.isNotEmpty(row.getTableCells()) && row.getTableCells().size() >= 2) {
                        /*配置内容**/
                        String text = row.getTableCells().get(1).getText();
                        if(StringUtils.hasText(text)) {
                            ti.setPrimary(this.indexInfo(text));
                        }
                    }
                });
            /*索引设置**/
            subList.stream().filter(row -> row.getTableCells().stream()
                .anyMatch(cell -> cell != null && StringUtils.hasText(cell.getText()) && cell.getText().contains("索引"))).findAny()
                .ifPresent(row -> {
                    /*至少需要2个单元格内容**/
                    if(CollectionUtils.isNotEmpty(row.getTableCells()) && row.getTableCells().size() >= 2) {
                        /*配置内容**/
                        String text = row.getTableCells().get(1).getText();
                        if(StringUtils.hasText(text)) {
                            List<TableInfoIndex> indexList = Stream.of(text.trim().split("\\s+")).filter(StringUtils::hasText).map(s -> this.indexInfo(s.trim()))
                                    .filter(Objects::nonNull).collect(Collectors.toList());
                            ti.setIndex(indexList);
                        }
                    }
                });
            return ti;
        }
        return null;
    }

    /***
     * 查询包含指定字符串的单元格下标
     * @param cells 判定单元格集合
     * @param str 判断字符串
     * @return 对应单元格下标或-1
     */
    private int containCheckIndex(List<XWPFTableCell> cells, String str) {
        if(CollectionUtils.isNotEmpty(cells) && StringUtils.hasText(str)) {
            for(int i = 0; i < cells.size(); i++) {
                XWPFTableCell cell = cells.get(i);
                if (cell != null && StringUtils.hasText(cell.getText()) && cell.getText().trim().contains(str)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 解析索引基础数据
     * @param text 解析字符串
     * @return 结果
     */
    private TableInfoIndex indexInfo(String text) {
        Objects.requireNonNull(text, "数据不能为空");
        TableInfoIndex tii = new TableInfoIndex();
        Matcher matcher = PATTERN_TABLE_NAME.matcher(text.trim());
        /*主键索引名称**/
        String indexName = text.trim();
        if(matcher.find()) {
            /*字段字符串**/
            String fieldsString = matcher.group(1);
            if(StringUtils.hasText(fieldsString)) {
                tii.setField(Stream.of(fieldsString.split(",")).filter(StringUtils::hasText).map(String::trim).collect(Collectors.toSet()));
            }
        }
        tii.setName(indexName);
        return tii;
    }
}
