package com.anemortalkid.yummers.util;

import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ListRepositoryRemoveDataAnswer<T> implements Answer<T> {

	private List<T> repositoryData;

	public ListRepositoryRemoveDataAnswer(List<T> repositoryData) {
		this.repositoryData = repositoryData;
	}

	@Override
	public T answer(InvocationOnMock invocation) throws Throwable {
		T data = (T) invocation.getArguments()[0];
		this.repositoryData.remove(data);
		return data;
	}

}
