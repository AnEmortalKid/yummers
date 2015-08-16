package com.anemortalkid.yummers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.postoffice.EmailTestSample;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private FoodPreferenceRepository foodPreferenceRepository;

	private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Associates stored= " + associateRepository.count());
		LOGGER.info("FoodPreferences stored= " + foodPreferenceRepository.count());
	}
}
