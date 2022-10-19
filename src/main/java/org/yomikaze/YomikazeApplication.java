package org.yomikaze;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@RequiredArgsConstructor
public class YomikazeApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(YomikazeApplication.class, args);
    }

}
