package cn.charlie166.spring.boot.web.service.impl;

import cn.charlie166.spring.boot.web.domain.*;
import cn.charlie166.spring.boot.web.exception.CustomException;
import cn.charlie166.spring.boot.web.service.TableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
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
    /*匹配索引正则表达式**/
    private Pattern PATTERN_INDEX_INFO = Pattern.compile("[(（]([\\w\\s\\u4e00-\\u9fa5,]+?)[)）]");
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
                final Path outFile = Paths.get("F:/out.sql");
                Files.deleteIfExists(outFile);
                if(Files.notExists(outFile))
                    Files.createFile(outFile);
                while (tablesIterator.hasNext()) {
                    XWPFTable table = tablesIterator.next();
                    String[] checkResult = this.checkDatabaseTable(table);
                    if(checkResult != null) {
                        if(checkResult[0].matches("[a-zA-z0-9_]+")){
                            log.debug("数据库表: \r\n{}----{}", checkResult[0], checkResult[1]);
                            TableInfo tableInfo = this.convert(table);
                            if(tableInfo != null) {
                                final String sql = this.toScript(tableInfo) + System.lineSeparator() + System.lineSeparator();
                                log.debug("sql: {}", sql);
                                Files.write(outFile, sql.getBytes(), StandardOpenOption.APPEND);
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
                        String [] tmp = new String[]{"", matcher.group(1).trim()};
                        tmp[0] = s.substring(0, matcher.start()).trim();
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
                        /*第一个单元格是空字符串的**/
                        if(startIndex > -1 && "".equals(firstCell.getText().trim())) {
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
                        field.setNotNull(row.getCell(indexInfo[5]).getText().replaceAll("\\s+", "").toLowerCase().equals("notnull"));
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
                        final List<XWPFParagraph> paragraphs = row.getTableCells().get(1).getParagraphs();
                        if(CollectionUtils.isNotEmpty(paragraphs)) {
                            List<TableInfoIndex> indexList = paragraphs.stream().map(XWPFParagraph::getText).filter(StringUtils::hasText).map(s -> this.indexInfo(s.trim()))
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
        Matcher matcher = PATTERN_INDEX_INFO.matcher(text.trim());
        if(matcher.find()) {
            /*字段字符串**/
            String fieldsString = matcher.group(1);
            if(StringUtils.hasText(fieldsString)) {
                tii.setField(Stream.of(fieldsString.split(",")).filter(StringUtils::hasText).map(String::trim).collect(Collectors.toSet()));
            }
            /*主键索引名称**/
            tii.setName(text.substring(0, matcher.start()));
            return tii;
        }
        return null;
    }

    /***
     * 将表对象数据转换为建表脚本语句
     * @param ti 表数据
     * @return 脚本语句
     */
    private String toScript(TableInfo ti) {
        Objects.requireNonNull(ti);
        /*表字段语句**/
        StringBuilder sql = new StringBuilder();
        /*表备注信息语句**/
        StringBuilder remark = new StringBuilder();
        if(CollectionUtils.isNotEmpty(ti.getFields())) {
            /*先添加表备注**/
            if(!StringUtils.isEmpty(ti.getLabel())) {
                remark.append("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'" + ti.getLabel() +
                    "' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'" + ti.getName() + "'")
                .append(System.lineSeparator()).append("GO").append(System.lineSeparator());
            }
            sql.append("/*==================================================================*/").append(System.lineSeparator());
            sql.append("/* Table:").append(ti.getName()).append("       ").append(!StringUtils.isEmpty(ti.getLabel()) ? ti.getLabel() : "")
                .append("        */").append(System.lineSeparator());
            sql.append("/*==================================================================*/").append(System.lineSeparator());
            sql.append("SET ANSI_NULLS ON").append(System.lineSeparator()).append("GO").append(System.lineSeparator());
            sql.append("SET QUOTED_IDENTIFIER ON").append(System.lineSeparator()).append("GO").append(System.lineSeparator());
            sql.append("CREATE TABLE ").append(ti.getName()).append(" (").append(System.lineSeparator());
            List<String> fieldList = new ArrayList<>(ti.getFields().size() + 1);
            ti.getFields().forEach(field -> {
                fieldList.add("\t" + field.getField() + "\t" + field.getType().toUpperCase() + "\t" +
                    (StringUtils.isEmpty(field.getDefaultVal()) ? "" : "DEFAULT '" + field.getDefaultVal() + "'") + "\t" +
                    (field.isNotNull() ? "NOT NULL" : "NULL"));
                if(!StringUtils.isEmpty(field.getChinese()) || !StringUtils.isEmpty(field.getRemark())){
                    String description = (!StringUtils.isEmpty(field.getChinese()) ? field.getChinese() : "") +
                        (!StringUtils.isEmpty(field.getRemark()) ? "{" + field.getRemark().replaceAll("\\s+", " ") + "}" : "");
                    remark.append("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'" + description +
                        "' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'" + ti.getName() +
                        "', @level2type=N'COLUMN',@level2name=N'" + field.getField() + "'")
                        .append(System.lineSeparator()).append("GO").append(System.lineSeparator());
                }
            });
            if(ti.getPrimary() != null) {
                fieldList.add("\tCONSTRAINT " + ti.getPrimary().getName() + " PRIMARY KEY CLUSTERED (" + System.lineSeparator() + "\t\t" +
                    ti.getPrimary().getField().stream().map(one -> one + " ASC").collect(Collectors.joining("," + System.lineSeparator() + "\t")) +
                    System.lineSeparator() + "\t" +
                    ")WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]");
            }
            sql.append(fieldList.stream().collect(Collectors.joining("," + System.lineSeparator()))).append(System.lineSeparator());
            sql.append(") ON [PRIMARY]").append(System.lineSeparator()).append("GO").append(System.lineSeparator());
            sql.append(remark.toString());
            /*这里只是索引, 不是唯一索引**/
            if(CollectionUtils.isNotEmpty(ti.getIndex())) {
                sql.append(ti.getIndex().stream().map(index -> "CREATE INDEX " +
                    index.getName() + " ON " + ti.getName() + "(" + index.getField().stream().collect(Collectors.joining(", ")) +
                    ") WITH FILLFACTOR=30" + System.lineSeparator() + "GO").collect(Collectors.joining(System.lineSeparator())));
            }
        }
        return sql.toString();
    }
}
