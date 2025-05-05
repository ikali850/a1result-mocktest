package in.a1result.mockexam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class A1resultMockExamTestApplication {

	public static void main(String[] args) {

		SpringApplication.run(A1resultMockExamTestApplication.class, args);

		System.out.println("started...");
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}
}
