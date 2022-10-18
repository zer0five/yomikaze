package io.gitlab.zeromatter.yomikaze;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class YomikazeApplicationTests {

    private final String baseUrl = "http://localhost:";
    @LocalServerPort
    private int port;

    private String getUrl(String path) {
        return baseUrl + port + path;
    }

    private String getUrl() {
        return baseUrl + port;
    }

    @Test
    void authorizationTest() {


    }

}
