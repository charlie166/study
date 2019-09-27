package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestLogger {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test1(){
        logger.debug("test..........");
    }
}
