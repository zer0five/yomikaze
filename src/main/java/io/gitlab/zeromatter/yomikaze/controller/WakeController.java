package io.gitlab.zeromatter.yomikaze.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WakeController {
    @RequestMapping("/wake")
    public @ResponseBody String wake() {
        return "ok"; // one punch man
    }
}
