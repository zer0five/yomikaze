package io.gitlab.zeromatter.yomikaze.task;

import org.jboss.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

public class WakeMyDyno implements Runnable {

    private static final Logger logger = Logger.getLogger(WakeMyDyno.class.getName());

    private final String[] urls;

    public WakeMyDyno(String... urls) {
        this.urls = urls;
    }

    @Override
    @Scheduled(fixedRate = 25, initialDelay = 25, timeUnit = TimeUnit.MINUTES)
    public void run() {
        logger.debug("Wake the f*** up dyno, we have a website to run!"); // cyber bug 2077
        try {
            RestTemplate restTemplate = new RestTemplate();
            for (String url : urls) {
                restTemplate.headForHeaders(url);
            }
            logger.debug("Hey! You're finally awake!"); // skyrim
        } catch (Exception e) {
            logger.debug("ahh, the dyno is not awake, we need to wake it up somehow");
            logger.debug("...");
            logger.debug("I think I found some clues: ", e);
        }
    }
}
