package org.egov.sunbird;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
@Import({ TracerConfiguration.class })
@SpringBootApplication
public class SunbirdApplication {
    public static void main(String[] args) {
        SpringApplication.run(SunbirdApplication.class, args);
    }
}
