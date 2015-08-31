package com.anemortalkid.yummers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.anemortalkid.yummers.accounts.Account;
import com.anemortalkid.yummers.accounts.AccountRepository;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(final AccountRepository accountRepository) {
		return new CommandLineRunner() {
			@Override
			public void run(String... arg0) throws Exception {
				Account guest = accountRepository.findByUsername("guest");
				if (guest == null) {
					accountRepository.save(new Account("guest", "guest", "ROLE_BASIC"));
				}
				Account superu = accountRepository.findByUsername("super");
				if (superu == null) {
					accountRepository.save(new Account("super", "super", "ROLE_SUPER"));
				}
				Account adminu = accountRepository.findByUsername("admin");
				if (adminu == null) {
					accountRepository.save(new Account("admin", "admin", "ROLE_ADMIN"));
				}
			}
		};
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Associates stored= " + associateRepository.count());
		LOGGER.info("FoodPreferences stored= " + foodPreferenceRepository.count());
	}
}
