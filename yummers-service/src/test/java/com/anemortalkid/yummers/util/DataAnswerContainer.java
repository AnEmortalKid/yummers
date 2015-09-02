package com.anemortalkid.yummers.util;

import java.util.List;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class DataAnswerContainer<T> {

	private List<T> repositoryData;

	/**
	 * @param repositoryData
	 */
	public DataAnswerContainer(List<T> repositoryData) {
		this.repositoryData = repositoryData;
	}
	
	
	public Answer<T> saveDataAnswer() {
		return DataAnswerFactory.createSaveDataAnswer(repositoryData);
	}
	
}
