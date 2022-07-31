package io.gitlab.zeromatter.yomikaze.controller;

import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log
@Controller
public class WakeController {
    @RequestMapping("/wake")
    public @ResponseBody String wake() {
        log.info("ok");
        return "ok"; // one punch man
    }
}
