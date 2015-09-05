package com.anemortalkid.yummers;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Value("${yummers.prod.super.user}")
	private String superUser;

	@Value("${yummers.prod.super.password}")
	private String superPassword;

	// @Value("${yummers.simulation.init}")
	// private boolean runSimulation;

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

				// for prod launching so local testers can have a super account
				// and test the api
				if (superUser != null) {
					Account superUserAcct = accountRepository.findByUsername(superUser);
					if (superUserAcct == null) {
						accountRepository.save(new Account(superUser, superPassword == null ? superUser : superPassword, "ROLE_SUPER"));
					}
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
