package tech.noetzold.SensorDataProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SensorDataProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorDataProcessorApplication.class, args);
	}

}
