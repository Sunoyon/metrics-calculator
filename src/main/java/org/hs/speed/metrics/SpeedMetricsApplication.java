package org.hs.speed.metrics;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpeedMetricsApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedMetricsApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpeedMetricsApplication.class, args);
        LOGGER.info("###########################################");
        LOGGER.info("##### Speed Metrics Analyser is ready #####");
        LOGGER.info("###########################################");

	}

    @Bean("ping")
    public Supplier<String> ping() {
        return () -> "pong";
    }
}
