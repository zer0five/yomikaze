package io.gitlab.zeromatter.yomikaze.task;

import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Log
public class WakeMyDyno implements Runnable {

    private final String[] urls;

    public WakeMyDyno(String... urls) {
        this.urls = urls;
    }

    @Override
    @Scheduled(fixedRate = 25, initialDelay = 25, timeUnit = TimeUnit.MINUTES)
    public void run() {
        log.info("Wake the f*** up dyno, we have a website to run!"); // cyber bug 2077
        try {
            RestTemplate restTemplate = new RestTemplate();
            for (String url : urls) {
                restTemplate.headForHeaders(url);
            }
            log.info("Hey! You're finally awake!"); // skyrim
        } catch (Exception e) {
            log.warning("ahh, the dyno is not awake, we need to wake it up somehow");
            log.warning("...");
            log.log(Level.WARNING, "I think I found some clues: ", e);
        }
    }
}
