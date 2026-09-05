package club.overheard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OverheardApplication {
    public static void main(String[] args) {
        SpringApplication.run(OverheardApplication.class, args);
    }
}
