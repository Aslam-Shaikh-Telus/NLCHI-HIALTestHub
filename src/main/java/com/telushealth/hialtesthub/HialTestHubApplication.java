package com.telushealth.hialtesthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mongodb.MongoClient;

@SpringBootApplication(scanBasePackages = "com.telushealth.hialtesthub")
public class HialTestHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(HialTestHubApplication.class, args);
	}

	public MongoClient getClient() {
		return new MongoClient("localhost", 27017);
	}

}
