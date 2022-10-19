package org.yomikaze.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@ConfigurationProperties(prefix = "yomikaze")
@Data
public class YomikazeProperties {

    private Security security = new Security();

    @Data
    @Component
    @ConfigurationProperties(prefix = "yomikaze.security")
    public static class Security {


        private Password password = new Password();

        @Data
        @Component
        @ConfigurationProperties(prefix = "yomikaze.security.password")
        public static class Password {

            private Encoder encoder = new Encoder();

            @Data
            @Component
            @ConfigurationProperties(prefix = "yomikaze.security.password.encoder")
            public static class Encoder {
                private PasswordEncoderType type = PasswordEncoderType.ARGON2;
                private Argon2 argon2 = new Argon2();
                private BCrypt bcrypt = new BCrypt();

                public enum PasswordEncoderType {
                    BCRYPT, ARGON2
                }

                @Data
                @Component
                @ConfigurationProperties(prefix = "yomikaze.security.password.encoder.argon2")
                public static class Argon2 implements Supplier<Argon2PasswordEncoder> {

                    private int saltLength = 16;
                    private int hashLength = 32;
                    private int parallelism = 1;
                    private int memory = 1 << 12;
                    private int iterations = 3;

                    public Argon2PasswordEncoder get() {
                        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
                    }

                }

                @Data
                @Component
                @ConfigurationProperties(prefix = "yomikaze.security.password.encoder.bcrypt")
                public static class BCrypt implements Supplier<PasswordEncoder> {

                    private int strength = 10;

                    public PasswordEncoder get() {
                        return new BCryptPasswordEncoder(strength);
                    }

                }

            }

        }

    }
}
