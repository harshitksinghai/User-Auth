package com.harshitksinghai.UserEntry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class UserEntryApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserEntryApplication.class, args);
	}

}
