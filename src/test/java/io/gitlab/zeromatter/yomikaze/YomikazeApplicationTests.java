package io.gitlab.zeromatter.yomikaze;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest
class YomikazeApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void authorizationTest() {
        TestRestTemplate restTemplate = new TestRestTemplate("user", "password");
    }

}
