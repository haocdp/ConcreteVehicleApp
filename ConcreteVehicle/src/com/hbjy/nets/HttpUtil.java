package com.hbjy.nets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.hbjy.utils.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {

	public static String BASE_URL = "http://61.183.9.107:4015/";// 实际地址
	public static final String INFOVEHICLE_URL = "InfoVehicleService.svc/InfoVehicle/";
	public static final String USERCLIENT_URL = "UserClientService.svc/UserClient/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	private static final int REQUEST_TIMEOUT = 6 * 1000;// 请求时间
	private static final int SO_TIMEOUT = 10 * 1000; // 超时时间

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		System.out.println("url地址：" + getAbsoluteUrl(url));
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	/**
	 * 绝对地址方式
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get2(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}

	public static InputStream getResponse(String url) {
		InputStream localInputStream = null;
		try {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					REQUEST_TIMEOUT);// ��ʱʱ��
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, SO_TIMEOUT);//
			DefaultHttpClient mClient = new DefaultHttpClient(
					localBasicHttpParams);
			HttpGet localHttpGet = new HttpGet(url);
			HttpResponse localHttpResponse = mClient.execute(localHttpGet);
			// JSON��ʽ
			localHttpGet.setHeader("Accept", "application/json");
			localHttpGet.setHeader("Content-type", "application/json");
			if (localHttpResponse.getStatusLine().getStatusCode() != 200) {
				localHttpGet.abort();
			} else {
				InputStream mInputStream = localHttpResponse.getEntity()
						.getContent();
				localInputStream = mInputStream;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localInputStream;
	}

	public static String getRespString(String url) throws IOException {
		Log.d("url", url);
		InputStream in = getResponse(url);
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];

		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	public static String getAddUriByLatLng(String latitude, String longitude) {
		String url = "http://api.map.baidu.com/geocoder?output=json&location="
				+ latitude + "," + longitude + "&key=" + Constant.MAPKEY;
		return url;
	}
	
	private static JSONObject geocodeAddr(String paramString) {
		StringBuilder localStringBuilder = new StringBuilder();
		String str = null;
		try {
			HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(paramString).openConnection();
			localHttpURLConnection.setConnectTimeout(5000);
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(
							localHttpURLConnection.getInputStream()));

			while ((str = localBufferedReader.readLine()) != null) {
				localStringBuilder.append(str);
			}
			localBufferedReader.close();
			localHttpURLConnection.disconnect();
			
		} catch (Exception e) {
			//Log.d("111", e.toString());
			System.out.println("错误消息："+e.toString());
			//e.printStackTrace();
		}
		JSONObject localJSONObject = null;
		try {
			localJSONObject = new JSONObject(localStringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return localJSONObject;

	}

	public static String getAddressByLatLng(String latitude, String longitude) {
		String localObject = null;
		JSONObject localJSONObject = geocodeAddr(getAddUriByLatLng(latitude, longitude));
		try {
			if (localJSONObject.get("status").equals("OK")) {
				localObject = localJSONObject.getJSONObject("result").getString("formatted_address");

			}
			return localObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
