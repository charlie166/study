package cn.charlie166.spring.boot.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 引导请求控制器
 */
@Controller
public class IndexController extends BaseController {

    /**
     * 首页请求
     * @return 页面地址
     */
    @GetMapping(value = {"", "/"})
    public String pageIndex(Model model) {
        LocalDateTime now = LocalDateTime.now();
        String nowString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(now);
        model.addAttribute("now", nowString);
        return "index/index";
    }
}
