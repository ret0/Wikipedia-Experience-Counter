package edu.mit.cci.wikipedia.experience;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLFetcher {
	
	private final static Logger LOG = LoggerFactory.getLogger(XMLFetcher.class);
	
	public static void main(String []args) {
	     String xmlResult = new XMLFetcher().executeHTTPRequest("http://www.google.com/");
	     System.out.println(xmlResult);
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

}
