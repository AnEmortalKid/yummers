package com.anemortalkid.yummers.util;

import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DataAnswerFactory {

	public static <T> Answer<T> createSaveDataAnswer(List<T> repositoryData) {
		return new ListRepositorySaveDataAnswer<T>(repositoryData);
	}

	private static class ListRepositorySaveDataAnswer<T> implements Answer<T> {
		private List<T> repositoryData;

		private ListRepositorySaveDataAnswer(List<T> repositoryData) {
			this.repositoryData = repositoryData;
		}

		@Override
		public T answer(InvocationOnMock invocation) throws Throwable {
			@SuppressWarnings("unchecked")
			T data = (T) invocation.getArguments()[0];
			repositoryData.add(data);
			return data;
		}
	}

}
