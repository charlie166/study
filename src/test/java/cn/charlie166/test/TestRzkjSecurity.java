package cn.charlie166.test;

import com.rz.cms.sys.security.SecurityManage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestRzkjSecurity {

    @Test
    public void test() {
        log.debug("测试开始...");
        String expireDateStart = "20190801";
        String expireDateEnd = "20210201";
        String mac = "E84E067FD36B";
        try {
            String s1 = SecurityManage.encryptUse3Des(mac);
            String s2 = SecurityManage.encryptUse3Des(expireDateStart);
            String s3 = SecurityManage.encryptUse3Des(expireDateEnd);
            String s4 = s2 + s1 + s3;
            String s5 = SecurityManage.encryptUseBaseMD5(s4);
            log.debug("加密码: {}", s4);
            log.debug("校验码: {}", s5);
        } catch (Exception e) {
            log.error("操作失败", e);
        }
    }
}
