package io.gitlab.zeromatter.yomikaze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YomikazeApplication {

    public static void main(String[] args) {
        SpringApplication.run(YomikazeApplication.class, args);
    }

}
