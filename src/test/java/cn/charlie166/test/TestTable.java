package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import cn.charlie166.spring.boot.web.service.TableService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
