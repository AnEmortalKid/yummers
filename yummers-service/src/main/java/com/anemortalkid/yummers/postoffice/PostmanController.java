package com.anemortalkid.yummers.postoffice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/postman")
public class PostmanController {

	@RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
	public void sendEmail(String userName, String password) {
		EmailTestSample ets = new EmailTestSample(userName, password);
		ets.sendEmail();
	}

}
