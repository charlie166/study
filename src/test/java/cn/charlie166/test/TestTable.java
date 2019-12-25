package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import cn.charlie166.spring.boot.web.service.TableService;
import org.apache.poi.xwpf.usermodel.*;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 建表语句测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestTable {

    @Autowired
    private TableService tableService;

    @Test
    public void testScript(){
        tableService.parseScriptFrom("F:/1.docx");
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
            document.write(new FileOutputStream("F:/2.docx"));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
