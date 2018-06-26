package com.hbjy.nets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class NetWork {
	 //public static final String BASE_URL = "http://59.69.105.73:12345/";//测试地址
	//public static final String BASE_URL = "http://59.69.105.50:12345/";// 测试地址2
	public static final String BASE_URL = "http://61.183.9.107:4015/";//实际地址
	public static final String INFOVEHICLE_URL = "InfoVehicleService.svc/InfoVehicle/";
	public static final String USERCLIENT_URL = "UserClientService.svc/UserClient/";

	public static DefaultHttpClient mClient;
	public static InputStream mInputStream;

	private static final int REQUEST_TIMEOUT = 6 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟

	// get方式
	public static InputStream getResponse(String url) {
		InputStream localInputStream = null;
		try {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					REQUEST_TIMEOUT);// 超时时间
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, SO_TIMEOUT);// 等待数据超时时间
			mClient = new DefaultHttpClient(localBasicHttpParams);
			HttpGet localHttpGet = new HttpGet(url);
			HttpResponse localHttpResponse = mClient.execute(localHttpGet);
			// JSON方式
			localHttpGet.setHeader("Accept", "application/json");
			localHttpGet.setHeader("Content-type", "application/json");
			if (localHttpResponse.getStatusLine().getStatusCode() != 200) {
				localHttpGet.abort();
			} else {
				mInputStream = localHttpResponse.getEntity().getContent();
				localInputStream = mInputStream;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localInputStream;
	}

	// post方式
	public static InputStream postResponse(String url,
			HashMap<String, Object> params) {
		InputStream localInputStream = null;
		HttpResponse localHttpResponse;
		try {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					15000);
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, 15000);
			mClient = new DefaultHttpClient(localBasicHttpParams);
			HttpPost localHttpPost = new HttpPost(url);
			ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>();
			Iterator<String> localIterator = null;
			// 遍历hashmap
			if ((params != null) && (params.size() > 0)) {
				localIterator = params.keySet().iterator();
				String str = null;
				do {
					if (!localIterator.hasNext()) {
						localArrayList.add(new BasicNameValuePair(str, params
								.get(str).toString()));
					}

					str = (String) localIterator.next();
				} while (params.get(str) == null);
				localHttpPost.setEntity(new UrlEncodedFormEntity(
						localArrayList, "UTF-8"));
				localHttpResponse = mClient.execute(localHttpPost);
				EntityUtils.toString(new DefaultHttpClient().execute(
						localHttpPost).getEntity());
				if (localHttpResponse.getStatusLine().getStatusCode() == 200)
					localHttpPost.abort();
				else {
					mInputStream = localHttpResponse.getEntity().getContent();
					localInputStream = mInputStream;
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();

		}
		return localInputStream;
	}

	public static String getResponseString(String url) throws IOException {
		Log.d("url", url);
		InputStream in = getResponse(url);
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];

		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	

	

}
