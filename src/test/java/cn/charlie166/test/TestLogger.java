package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestLogger {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test1(){
        logger.debug("test..........");
    }

    @Test
    public void test2() {
        BigInteger bi = new BigInteger("2");
        bi.add(new BigInteger(5 + ""));
        logger.debug("result:" + bi);
    }

    @Test
    public void test3(){
        IntStream.range(1, 11).forEach(i -> {
            logger.debug("i:" + i);
        });
    }

    @Test
    public void test4(){
        Integer i1 = new Integer(13333);
        Integer i2 = Integer.valueOf(232323);
        logger.debug("check:" + (i1.getClass() == i2.getClass()));
    }
}
