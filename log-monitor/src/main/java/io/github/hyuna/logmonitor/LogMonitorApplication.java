package io.github.hyuna.logmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LogMonitorApplication {
// 
	public static void main(String[] args) {
		SpringApplication.run(LogMonitorApplication.class, args);
	}

}
