package org.ncibi.cytoscape.mimi.plugin;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;

public class DatabaseQueryTest {

	private static final String QUERY_FOR_QUERY_BY_SYMBOL = "csf1r/////Homo sapiens/////All Molecule Types/////All Data Sources/////1. Query genes + nearest neighbors";
	private static final int QUERY_TYPE_BY_SYMBOL = 0;

	@Test
	public void basicQueryTest1() throws IOException {

		QueryMiMI.doQuery(QUERY_TYPE_BY_SYMBOL, QUERY_FOR_QUERY_BY_SYMBOL);

		BufferedReader in = QueryMiMI.rd;

		String line;
		while ((line = in.readLine()) != null) {
			System.out.println("->" + line);
		}
	}

}