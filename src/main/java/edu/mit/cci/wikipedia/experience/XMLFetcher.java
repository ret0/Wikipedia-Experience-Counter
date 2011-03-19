package edu.mit.cci.wikipedia.experience;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import edu.mit.cci.wikipedia.experience.util.MapSorter;
import edu.mit.cci.wikipedia.experience.xml.Api;

public class XMLFetcher {
	
	private static final String ANSWER_FORMAT = "&format=xml";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String LANG_CODE = "en";
	private static final int MAX_NUMBER_OF_EDITORS = 20;
	
	private final static Logger LOG = LoggerFactory.getLogger(XMLFetcher.class);
	
	public static void main(String []args) throws Exception {
		XMLFetcher xmlFetcher = new XMLFetcher();
		// ----------------
		// PAGE NAME
		// ----------------
		String pageName = "The_Beatles";
		
		List<String> topEditorsForPage = xmlFetcher.getTopEditorsForPage(LANG_CODE, pageName);
		
		for (String userName : topEditorsForPage) {
			String userDetailsXML = xmlFetcher.getUserDetails(userName);
			Api deserializedUsers = xmlFetcher.deserialize(userDetailsXML);
			Long userScore = deserializedUsers.generateScoreForUser();
			System.out.println(userScore);
		}
	}

	private String getUserDetails(String userName) throws UnsupportedEncodingException {
		return executeHTTPRequest(generateUserDetailsRequestURL(LANG_CODE, userName));
	}

	/**
	 * @return List of the top MAX_NUMBER_OF_EDITORS user names with the 
	 * most edits for the given page
	 */
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
		return "http://" + langCode
				+ ".wikipedia.org/w/api.php?action=query&prop=revisions&titles="
				+ pageName + "&rvprop=user&rvlimit=500" + ANSWER_FORMAT;
	}
	
	/**
	 * editcount and reg date
	 */
	private String generateUserDetailsRequestURL(String langCode, String userName) throws UnsupportedEncodingException {
		String userFields = URLEncoder.encode("editcount|registration", DEFAULT_ENCODING);
		String userNameEncoded = URLEncoder.encode(userName, DEFAULT_ENCODING);
		return "http://" + langCode
				+ ".wikipedia.org/w/api.php?action=query&list=users&ususers="
				+ userNameEncoded + "&usprop=" + userFields + ANSWER_FORMAT;
	}

	/**
	 * Executes HTTP Request and returns contents as String
	 */
	private String executeHTTPRequest(String url) {
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
	
	/**
	 * Turns XML String into a Java object
	 */
	private Api deserialize(String s) throws Exception {
		return new Persister().read(Api.class, s);
	}

}
