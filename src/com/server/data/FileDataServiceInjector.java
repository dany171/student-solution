package com.server.data;

import com.search.BasicStudentSearchService;
import com.server.CommandExecutor;

public class FileDataServiceInjector implements DataServiceInjector{

	@Override
	public Consumer getConsumer() {
		return new CommandExecutor(new FileDataService());
	}

}