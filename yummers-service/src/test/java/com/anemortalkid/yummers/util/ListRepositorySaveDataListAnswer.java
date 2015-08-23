package com.anemortalkid.yummers.util;

import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ListRepositorySaveDataListAnswer<T> implements Answer<T> {

	private List<T> repositoryData;

	public ListRepositorySaveDataListAnswer(List<T> repositoryData) {
		this.repositoryData = repositoryData;
	}

	@Override
	public T answer(InvocationOnMock invocation) throws Throwable {
		List argList = invocation.getArgumentAt(0, List.class);
		repositoryData.addAll(argList);
		return null;
	}

}
