package org.yomikaze.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Profile("heroku")
@Slf4j
public class WakeMyDyno implements Runnable {

    private final String[] urls;

    public WakeMyDyno(String... urls) {
        this.urls = urls;
    }

    @Override
    @Scheduled(fixedRate = 25, initialDelay = 25, timeUnit = TimeUnit.MINUTES)
    public void run() {
        log.debug("Wake the f*** up dyno, we have a website to run!"); // cyber bug 2077
        try {
            RestTemplate restTemplate = new RestTemplate();
            for (String url : urls) {
                restTemplate.headForHeaders(url);
            }
            log.debug("Hey! You're finally awake!"); // skyrim
        } catch (Exception e) {
            log.debug("ahh, the dyno is not awake, we need to wake it up somehow");
            log.debug("...");
            log.debug("I think I found some clues: ", e);
        }
    }
}
