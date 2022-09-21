package io.gitlab.zeromatter.yomikaze;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;

@SpringBootApplication
@RequiredArgsConstructor
public class YomikazeApplication extends SpringBootServletInitializer {

    private final MessageSource messageSource;

    public static void main(String[] args) {
        SpringApplication.run(YomikazeApplication.class, args);
    }

}
