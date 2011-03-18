package edu.mit.cci.wikipedia.experience;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private final static Logger LOG = LoggerFactory.getLogger(XMLFetcher.class);
	
	public static void main(String []args) throws Exception {
		renameMethodLAAATER("en", "Northeastern_University");
	}

	private static void renameMethodLAAATER(String langCode, String pageName)
			throws Exception {
		String requestURL = generateRequestURL(langCode, pageName);
		XMLFetcher xmlFetcher = new XMLFetcher();
		String xmlResult = xmlFetcher.executeHTTPRequest(requestURL);
		Api xmlResultObject = xmlFetcher.deserialize(xmlResult);
		Map<String, Integer> ranking = xmlResultObject
				.generateRankingOfAllNonAnonymousUsers();
		for (Entry<String, Integer> rankingEntry : ranking.entrySet()) {
			System.out.println(rankingEntry.getKey() + " = "
					+ rankingEntry.getValue());
		}
	}

	/**
	 * Last 500 changes to page
	 */
	private static String generateRequestURL(String langCode, String pageName) {
		return "http://" + langCode + ".wikipedia.org/w/api.php?action=query&prop=revisions&titles=" + pageName + "&rvprop=user&rvlimit=500&format=xml";
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
