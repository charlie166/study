package cn.charlie166.test;

import cn.charlie166.spring.boot.web.BootApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 通用测试类
 */
@Slf4j
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

    @Test
    public void testString() {
        StringBuilder sb = new StringBuilder();
        Object s = "";
        System.out.println("v1:" + (sb.toString() == ""));
        System.out.println("v2:" + (s == ""));
    }

    @Test
    public void testList() {
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("1", new ArrayList<>(Arrays.asList(22)));
        final List<Integer> list = map.get("1");
        list.add(44);
        System.out.println("size:" + map.get("1").size());
    }

    @Test
    public void testSort() {
        final List<Integer> list = Arrays.asList(1, 4, 5, 35, 66, 32, 6, 6, 44, Integer.MAX_VALUE, 45);
        log.debug("原来: {}", list);
        list.sort(Comparator.comparingInt(a -> a));
        log.debug("排序后: {}", list);
    }

    @Test
    public void testSet() {
        Set<String> s = new HashSet<>();
        s.add("2");
        s.add(null);
        s.add(null);
        s.add("gg");
        log.debug("size:" + s.size());
    }

    @Test
    public void testBetween() {
        LocalDateTime d1 = LocalDateTime.now();
        LocalDateTime d2 = d1.minusDays(2).minusMinutes(55).minusSeconds(34).minusHours(3);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.debug("d: {}, {}", dtf.format(d1), dtf.format(d2));
        log.debug("result: {}", ChronoUnit.MINUTES.between(d1, d2));
    }

    @Test
    public void testCharCode() {
        String s0 = "onclick";
        s0.chars().forEach(i -> log.debug("a: {}", i));
        String s1 = "οnclick";
        s1.chars().forEach(i -> log.debug("b: {}", i));
    }

    @Test
    public void testReplace() {
        StringBuilder sb = new StringBuilder("%___%");
        log.debug("result: {}", sb.replace(1, 2, "0"));
    }

}
