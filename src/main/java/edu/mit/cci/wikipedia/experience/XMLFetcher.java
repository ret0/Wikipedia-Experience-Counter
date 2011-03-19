package edu.mit.cci.wikipedia.experience;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.cci.wikipedia.experience.xml.Api;

public class XMLFetcher {
	
	private static final String LANG_CODE = "en";
	private static final int MAX_NUMBER_OF_EDITORS = 20;
	
	private final static Logger LOG = LoggerFactory.getLogger(XMLFetcher.class);
	
	public static void main(String []args) throws Exception {
		XMLFetcher xmlFetcher = new XMLFetcher();
		List<String> topEditorsForPage = xmlFetcher.getTopEditorsForPage(LANG_CODE, "Northeastern_University");
		
		for (String userName : topEditorsForPage) {
			System.out.println(xmlFetcher.getUserDetails(userName));
		}
	}

	private String getUserDetails(String userName) {
		return executeHTTPRequest(generateUserDetailsRequestURL(LANG_CODE, userName));
	}

	private List<String> getTopEditorsForPage(String langCode, String pageName)
			throws Exception {
		String requestURL = generateRevisionRequestURL(langCode, pageName);
		
		String xmlResult = executeHTTPRequest(requestURL);
		Api xmlResultObject = deserialize(xmlResult);
		Map<String, Integer> ranking = xmlResultObject
				.generateRankingOfAllNonAnonymousUsers();
		
		Map<String, Integer> sortedRanking = new MapSorter<String, Integer>().sortByValue(ranking);
		
		Iterator<String> iterator = sortedRanking.keySet().iterator();
		List<String> topUsers = new LinkedList<String>();
		for(int i = 0; i < MAX_NUMBER_OF_EDITORS && iterator.hasNext(); i++) {
			topUsers.add(iterator.next());
		}
		return topUsers;
	}


	/**
	 * Last 500 changes to page, no date restrictions
	 */
	private String generateRevisionRequestURL(String langCode, String pageName) {
		return "http://" + langCode + ".wikipedia.org/w/api.php?action=query&prop=revisions&titles=" + pageName + "&rvprop=user&rvlimit=500&format=xml";
	}
	
	/**
	 * editcount and reg date
	 */
	private String generateUserDetailsRequestURL(String langCode, String userName) {
		//http://en.wikipedia.org/w/api.php?action=query&list=users&ususers=Dogdogkun&usprop=editcount|registration
		return "http://" + langCode + ".wikipedia.org/w/api.php?action=query&list=users&ususers=Dogdogkun&usprop=editcount|registration&format=xml";
	}

	public String executeHTTPRequest(String url) {
		HttpClient httpclient = new DefaultHttpClient();
	        try {
	            HttpGet httpget = new HttpGet(url);
	            LOG.debug("executing request " + httpget.getURI());
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            return httpclient.execute(httpget, responseHandler);
	        } catch (ClientProtocolException e) {
				LOG.error("ClientProtocolException", e);
			} catch (IOException e) {
				LOG.error("IOException", e);
			} finally {
	            httpclient.getConnectionManager().shutdown();
	        }
		LOG.error("Problem while executing request");
		return "";
	}
	
	public Api deserialize(String s) throws Exception {
		return new Persister().read(Api.class, s);
	}

}
