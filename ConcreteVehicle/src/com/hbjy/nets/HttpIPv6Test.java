package com.hbjy.nets;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpIPv6Test {

	public static void get(final String url, final HttpResponseHandler httpResponseHandler) {

		new Thread(new Runnable() {
			public void run() {
				try {

					HttpClient httpClient = new DefaultHttpClient();
					URI uri = new URI(url);
					HttpGet httpGet = new HttpGet(uri);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpEntity = httpResponse.getEntity();
						String response = EntityUtils.toString(httpEntity, "utf-8");
						httpResponseHandler.onSuccess(200, response);
					} else {
						httpResponseHandler.onFailure(httpResponse.getStatusLine().getStatusCode());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public static void get2(final String address, final HttpResponseHandler httpResponseHandler) {
		new Thread(new Runnable() {
			public void run() {
				try {
					HttpURLConnection connection = null;
					URL url = new URL(address);
					
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					if (httpResponseHandler != null) {
						httpResponseHandler.onSuccess(200, response.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
