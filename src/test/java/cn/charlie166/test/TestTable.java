package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import cn.charlie166.spring.boot.web.service.TableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 建表语句测试类
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestTable {

    @Autowired
    private TableService tableService;

    @Test
    public void testScript(){
        tableService.parseScriptFrom("F:/db_info/1.docx");
    }


    @Test
    public void testReadWord() {
        String filepath = "F:/1.1.docx";
        try(XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(filepath)))){
            final XWPFNumbering numbering = doc.getNumbering();
            doc.getParagraphs().forEach(p -> {
                final BigInteger numID = p.getNumID();
                log.debug("numID: {}", numID);
                final List<XWPFRun> runs = p.getRuns();
                log.debug("size: {}", runs.size());
                if(runs.size() > 0) {
                                    final XWPFRun run = runs.get(0);
                                    log.debug("run0: {}", run.getTextPosition());
                                }
            });
            log.debug("table...");
            doc.getTables().forEach(t -> {
                final List<XWPFTableRow> rows = t.getRows();
                log.debug("rows: {}", rows.size());
                if(CollectionUtils.isNotEmpty(rows)) {
                    if(rows.size() > 3) {
                        final XWPFTableRow row = rows.get(2);
                        final List<XWPFTableCell> cells = row.getTableCells();
                        cells.forEach(cell -> {
                            final List<XWPFParagraph> paragraphs = cell.getParagraphs();
                            log.debug("cell p: {}", paragraphs.size());
                            if(paragraphs.size() > 0) {
                                final XWPFParagraph paragraph = paragraphs.get(0);
                                final List<XWPFRun> runs = paragraph.getRuns();
                                log.debug("paragraph size: {}", runs.size());
                                if(runs.size() > 0) {
                                    final XWPFRun run = runs.get(0);
                                    log.debug("run: {}", run.getTextPosition());
                                }
                            }
                        });
                    } else {
                        log.debug("小于");
                    }
                }
            });
        } catch (IOException e) {
            log.error("操作出现异常", e);
        }
    }

    @Test
    public void testCreateWord() {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("The list:");

        ArrayList<String> documentList = new ArrayList<String>(
                Arrays.asList(
                        new String[]{
                                "One",
                                "Two",
                                "Three"
                        }));

        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
        //Next we set the AbstractNumId. This requires care.
        //Since we are in a new document we can start numbering from 0.
        //But if we have an existing document, we must determine the next free number first.
        cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));


        CTLvl cTLvl = cTAbstractNum.addNewLvl();
        cTLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
        cTLvl.addNewLvlText().setVal("%1.");
        cTLvl.addNewStart().setVal(BigInteger.valueOf(1));

        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);

        XWPFNumbering numbering = document.createNumbering();

        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);

        BigInteger numID = numbering.addNum(abstractNumID);

        for (String string : documentList) {
            paragraph = document.createParagraph();
            paragraph.setNumID(numID);
            run = paragraph.createRun();
            run.setText(string);
        }

        try {
            document.write(new FileOutputStream("F:/3.docx"));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRead() {
        String filepath = "F:/3.docx";
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(filepath)))){
            final XWPFNumbering numbering = doc.getNumbering();
            log.debug("p size: {}", doc.getParagraphs().size());
            doc.getParagraphs().forEach(p -> {
                final List<XWPFRun> runs = p.getRuns();
                log.debug("p num id: {}, runs size: {}, text: {}", p.getNumID(), runs.size(), p.getText());
                runs.forEach(run -> {
                    log.debug("run position: {}, text: {}", run.getTextPosition(), run.toString());
                });
            });
        } catch (IOException e) {
            log.error("操作出现异常", e);
        }
    }

    @Test
    public void testWordToHtml() {
        String fromWord = "F:/1.docx";
        String targetHtml = "F:/out/1.html";
        final Path targetPath = Paths.get(targetHtml);
        if(!Files.notExists(targetPath)) {
            try {
                Files.createFile(targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(OutputStream os = Files.newOutputStream(targetPath)) {
            WordprocessingMLPackage wordMLPackage = Docx4J.load(Paths.get(fromWord).toFile());
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            String imageFilePath = targetHtml.substring(0, targetHtml.lastIndexOf("/") + 1) + "/images"; // 存放图片的文件夹的路径
            htmlSettings.setImageDirPath(imageFilePath);
            htmlSettings.setImageTargetUri("images"); // img 中 src 路径的上一级路径
            htmlSettings.setWmlPackage(wordMLPackage);
            String userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img,  ol, ul, li, table, caption, tbody, tfoot, thead, tr, th, td " +
                    "{ margin: 0; padding: 0; border: 0;}" +
                    "body {line-height: 1; padding: 30px;} ";
            htmlSettings.setUserCSS(userCSS); // 用户自己定义的 CSS
            Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);
            Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
        } catch (Docx4JException | IOException e) {
            log.error("操作失败", e);
        }
        log.error("完成...");
    }

}
