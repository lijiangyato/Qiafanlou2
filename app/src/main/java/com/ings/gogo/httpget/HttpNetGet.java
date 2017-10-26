package com.ings.gogo.httpget;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import com.ings.gogo.base.BaseNet;
import com.ings.gogo.base.BaseParser;
import com.ings.gogo.base.OnNetCompleteListener;

public class HttpNetGet extends BaseNet {
	public HttpNetGet(String url, Map<String, String> nameValuesPairs,
			BaseParser parser, OnNetCompleteListener listener) {
		super(url, nameValuesPairs, parser, listener);
		if (nameValuesPairs != null && nameValuesPairs.size() != 0) {
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
			if (!url.endsWith("?")) {
				url += "?";
			}
			StringBuilder stringBuilder = new StringBuilder(url);
			Set<String> keys = nameValuesPairs.keySet();
			for (String key : keys) {
				String value = nameValuesPairs.get(key);
				stringBuilder.append(key + "=" + value + "&");
			}
			url = stringBuilder.substring(0, stringBuilder.length() - 1);

			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				handler.sendEmptyMessage(BaseNet.NET_ERROR);
			}
		}
	}

	public HttpNetGet(String url, BaseParser parser,
			OnNetCompleteListener listener) {
		super(url, parser, listener);
	}

	@Override
	public void netting() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Log.e("HttpGet", "ok1");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					// connection.addRequestProperty("Cookie",
					// LoginActivity.aspnetauth + ";"
					// + LoginActivity.ASP_NET_SessionId);
					// L.e("httpGet--->aspnetauth", LoginActivity.aspnetauth);
					// L.e("httpGet---->ASP.NET_SessionId",
					// LoginActivity.ASP_NET_SessionId);
					connection.setConnectTimeout(6000);
					connection.setReadTimeout(6000);
					if (connection.getResponseCode() == 200) {
						Log.e("HttpGet", "ok2");
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(inputStream));
						StringBuilder result = new StringBuilder();
						String temp = null;
						while ((temp = reader.readLine()) != null) {
							Log.e("HttpGet", "ok3");
							result.append(temp);
						}

						Object object = parser.parse(result.toString());
						Message msg = handler.obtainMessage();
						msg.what = BaseNet.NET_OK;
						msg.obj = object;
						handler.sendMessage(msg);
						Log.e("HttpGet", "ok4");
						reader.close();
					} else {

						handler.sendEmptyMessage(BaseNet.NET_ERROR);
					}
					connection.disconnect();
				} catch (IOException e) {
					handler.sendEmptyMessage(BaseNet.NET_ERROR);
				}
			}
		};
		// new Thread(runnable).start();
		executorService.execute(runnable);
	}
}
