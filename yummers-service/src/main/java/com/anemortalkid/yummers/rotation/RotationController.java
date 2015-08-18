package com.anemortalkid.yummers.rotation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;

@RestController
@RequestMapping("/rotations")
public class RotationController {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private RotationRepository rotationRepository;

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public YummersResponseEntity<Rotation> getCurrentRotation() {
		String callingPath = "/rotations/current";

		List<Rotation> activeRotations = rotationRepository.findByActive(true);
		if (activeRotations.isEmpty()) {
			return ResponseFactory.respondFail(callingPath, "no current rotations available");
		}
		if (activeRotations.size() > 1) {
			LOGGER.error("There are more active rotations (" + activeRotations.size() + ") than there should be.");
		}

		Rotation current = activeRotations.get(0);
		return ResponseFactory.respondOK(callingPath, current);
	}

	@RequestMapping(value = "/past", method = RequestMethod.GET)
	public YummersResponseEntity<List<Rotation>> getPastRotations() {
		String callingPath = "/rotations/past";

		List<Rotation> inactiveRotations = rotationRepository.findByActive(false);
		return ResponseFactory.respondOK(callingPath, inactiveRotations);
	}

	public void insertNewRotation(Rotation newRotation) {
		YummersResponseEntity<Rotation> currRotation = getCurrentRotation();
		if (currRotation.getBody() != null) {
			currRotation.getBody().setActive(false);
			rotationRepository.save(currRotation.getBody());
		} else {
			rotationRepository.save(newRotation);
		}
	}

	public boolean shouldRegenerate() {
		// TODO: don't handle this with some boolean, do some logic based on
		// preferences and associates and what not
		return getCurrentRotation().getBody() != null ? getCurrentRotation().getBody().isShouldRebuild() : false;
	}

}
