package com.anemortalkid.yummers.foodevent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foodevents")
public class FoodEventController {

	@Autowired
	private FoodEventRepository foodEventRepository;

	public List<FoodEvent> saveNewEvents(List<FoodEvent> newEvents) {
		// remove active ones first
		List<FoodEvent> previousActive = foodEventRepository.findByIsActive(true);
		previousActive.forEach(foodEvent -> foodEvent.setActive(false));
		foodEventRepository.save(previousActive);

		// add new events
		newEvents.forEach(foodEvent -> foodEvent.setActive(true));
		foodEventRepository.save(newEvents);
		return newEvents;
	}

	public List<FoodEvent> getActiveEvents() {
		return foodEventRepository.findByIsActive(true);
	}
}
