package com.dimentrix.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class ReportApplication {
	public static void setWorkingDir() {
		File workingDir = new File(System.getProperty("user.home")+"/.smartShipReports");
		if(!workingDir.exists())
			workingDir.mkdir();
		File tempDir = new File(System.getProperty("user.home")+"/.smartShipReports/.temp");
		if(!tempDir.exists())
			tempDir.mkdir();
	}
	public static void main(String[] args) {
		setWorkingDir();
		SpringApplication.run(ReportApplication.class, args);
	}

}