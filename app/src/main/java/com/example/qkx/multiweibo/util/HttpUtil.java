package com.example.qkx.multiweibo.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by qkx on 2015/10/1.
 */
public class HttpUtil {
    public static void sendHttpquest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish()方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        // 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    public static void sendHttpClientGET(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String respose = EntityUtils.toString(entity,"utf-8");
                        listener.onFinish(respose);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(e);
                }
            }
        }).start();
    }
    public static void sendHttpClientPOST(final String address, final List<NameValuePair> pairs,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(address);


                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,"utf-8");
                    httpPost.setEntity(entity);

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity responseentity = httpResponse.getEntity();
                    String respose = EntityUtils.toString(responseentity,"utf-8");
                    listener.onFinish(respose);
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(e);
                }
            }
        }).start();
    }
//    public static void sendHttpClientPOST(final String address,final HttpCallbackListener listener){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(address);
//
//                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//                    pairs.add(new BasicNameValuePair("client_id","3702692012"));
//                    pairs.add(new BasicNameValuePair("client_secret","ce59540048421e4392cce0be0a639890"));
//                    pairs.add(new BasicNameValuePair("grant_type","authorization_code"));
//                    pairs.add(new BasicNameValuePair("code","af6f8451046a71f732ac4f475c6e1346"));
//                    pairs.add(new BasicNameValuePair("redirect_uri","https://api.weibo.com/oauth2/default.html"));
//
//                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,"utf-8");
//                    httpPost.setEntity(entity);
//
//                    HttpResponse httpResponse = httpClient.execute(httpPost);
////                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                        HttpEntity responseentity = httpResponse.getEntity();
//                        String respose = EntityUtils.toString(responseentity,"utf-8");
//                        listener.onFinish(respose);
////                    }else {
////                        Log.v("3333","3333");
////                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    listener.onError(e);
//                }
//            }
//        }).start();
//    }
}
