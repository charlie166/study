package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通用测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestCommon {

    @Test
    public void testType(){
        Integer i = Integer.valueOf(3444);
        System.out.println("done:" + (i.getClass() == Integer.class));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse("2019-07-11 00:00:00");
            System.out.println("time:" + date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
