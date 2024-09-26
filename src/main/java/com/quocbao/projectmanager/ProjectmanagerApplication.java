package com.quocbao.projectmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.quocbao.projectmanager")
public class ProjectmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectmanagerApplication.class, args);
	}

}
