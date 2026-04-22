package com.Sorensen.FitMark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FitMarkApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitMarkApplication.class, args);
	}

}
