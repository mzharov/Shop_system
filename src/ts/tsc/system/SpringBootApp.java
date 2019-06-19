package ts.tsc.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ts.tsc.system")
public class SpringBootApp {
    public static void main(String... args) {
        SpringApplication.run(SpringBootApp.class, args);
    }
}
