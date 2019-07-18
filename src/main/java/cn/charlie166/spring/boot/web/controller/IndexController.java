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
     * @return
     */
    @GetMapping(value = {"", "/"})
    public String pageIndex(Model model) {
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("now", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(now));
        return "index/index";
    }
}
