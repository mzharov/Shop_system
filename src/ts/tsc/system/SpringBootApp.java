package ts.tsc.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "ts.tsc.system")
public class SpringBootApp {
    public static void main(String... args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringBootApp.class, args);
        ctx.close();
    }
}
