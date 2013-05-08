package org.ncibi.cytoscape.mimi.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.TaskMonitor;
import org.junit.Test;
import org.ncibi.cytoscape.mimi.MiMI;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.task.AbstractMiMIQueryTask;

public class DatabaseQueryTest {

	@Test
	public void versionTest() throws MalformedURLException, IOException {
		StreamUtil streamUtil = new StreamUtilShqdow();
		InputStream stream = streamUtil.getInputStream(new URL(MiMI.DBACCESSVERSION));
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line="";
		String version = null;
		while ((line = br.readLine()) != null){
			if (version == null) version = line;
			System.out.println(line);
		}
		br.close();
		Assert.assertEquals(MiMI.VERSION,version);
	}
	
	@Test
	public void basicQueryByName() throws Exception {
		QueryType type = QueryType.QUERY_BY_NAME;
		String queryString = "csf1r/////Homo sapiens/////All Molecule Types/////All Data Sources/////1. Query genes + nearest neighbors";
		String[] items = {"CSF1R","CALB2","CBL","FYN","GRB2","HTR2C","INPP5D","INPPL1","LYN","PIK3R1","RASA1"};
		queryForItems(type, queryString, items);
	}
	
	@Test
	public void basicQueryById() throws Exception {
		QueryType type = QueryType.QUERY_BY_ID;
		String queryString = "1436";
		String[] items = {"CSF1R","CALB2","CBL","FYN","GRB2","HTR2C","INPP5D","INPPL1","LYN","PIK3R1","RASA1"};
		queryForItems(type, queryString, items);
	}
	
	
	private void queryForItems(QueryType type, String queryString, String[] items) throws Exception {

		boolean[] hits = new boolean[items.length];
		
		for (int i = 0; i < hits.length; i++)
			hits[i] = false;

		QueryShadow gateway = new QueryShadow();
		gateway.doQuery(type, queryString);
		BufferedReader in = gateway.getReader();
		
		String line;
		while ((line = in.readLine()) != null) {
			for (int i = 0; i < hits.length; i++){
				System.out.println(line);
				if (line.contains(items[i])) hits[i] = true;
			}
		}
		gateway.closeReader();
		
		for (int i = 0; i < hits.length; i++) {
			Assert.assertTrue("Query type: " + type + ";\n" +
					"Query text " + queryString + ";\n" +
					"Did not fine expected query match: " + items[i], hits[i]);
		}
	}
	
	private class QueryShadow extends AbstractMiMIQueryTask {
		
		private StreamUtil streamUtil = new StreamUtilShqdow();
		private TaskMonitor taskMonitor = new DummyTaskMonitor();
		
		QueryShadow() {
			super();
		}

		public void doQuery(QueryType queryType, String queryForQueryBySymbol) throws Exception {
			super.doQuery(queryType, queryForQueryBySymbol, streamUtil, taskMonitor);
		}

		public BufferedReader getReader() {
			return super.rd;
		}

		public void closeReader() throws IOException {
			super.rd.close();
		}

		@Override
		public void run(TaskMonitor arg0) throws Exception {
			
		}
		
	}
	
	private class StreamUtilShqdow implements StreamUtil {

		public InputStream getInputStream(String source) throws IOException {
			URL url = new URL(source);
			return getInputStream(url);
		}

		public InputStream getInputStream(URL source) throws IOException {
			URLConnection conn = getURLConnection(source);
			return conn.getInputStream();
		}

		public URLConnection getURLConnection(URL source) throws IOException {
			return source.openConnection();
		}

	}
	
	private class DummyTaskMonitor implements TaskMonitor {

		public void setTitle(String title) {
		}

		public void setProgress(double progress) {
		}

		public void setStatusMessage(String statusMessage) {
		}
		
	}

}
