package com.anemortalkid.yummers.util;

import java.util.List;

import org.mockito.stubbing.Answer;

import com.anemortalkid.yummers.slots.Slot;

/**
 * 
 *
 */
public class DataAnswerFactory {

	public static <T> Answer<T> createSaveDataAnswer(List<T> repositoryData) {
		return new ListRepositorySaveDataAnswer<T>(repositoryData);
	}

	public static <T> Answer<T> removeDataAnswer(List<T> repositoryData) {
		return new ListRepositoryRemoveDataAnswer<T>(repositoryData);
	}

}
