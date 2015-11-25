package com.example.qkx.multiweibo.previous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.db.WeiboDB;
import com.example.qkx.multiweibo.model.User;
import com.example.qkx.multiweibo.util.HttpCallbackListener;
import com.example.qkx.multiweibo.util.HttpUtil;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 2015/11/1.
 */
public class QueryAcessToken extends Activity {
    private static final String TAG = "QueryAcessToken";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAcessToken();
    }
    private void getAcessToken() {
        String code = getCode();
        String acess_token_url = Constans.ACESS_TOKEN_URL;
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("client_id",Constans.CLIENT_ID));
        pairs.add(new BasicNameValuePair("client_secret",Constans.CLIENT_SECRECT));
        pairs.add(new BasicNameValuePair("grant_type","authorization_code"));
        pairs.add(new BasicNameValuePair("code",code));
        pairs.add(new BasicNameValuePair("redirect_uri",Constans.REDIRECT_URI));
        HttpUtil.sendHttpClientPOST(acess_token_url, pairs, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d(TAG,"response ----->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final String access_token = jsonObject.getString("access_token");
                    saveData("access_token", access_token);

                    Gson gson = new Gson();
                    User user = gson.fromJson(response, User.class);
                    WeiboDB weiboDB = WeiboDB.getInstance(getApplicationContext());
                    weiboDB.saveUser(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG,"response failed ----->" + e.getMessage());
            }
        });
        Intent intent = new Intent(this,FirstActivity.class);
        startActivity(intent);
        finish();
    }
    private String getCode() {
        Intent intent = getIntent();
        String data = intent.getDataString();
        String[] S = data.split("=");
        return S[1];
    }
    private void saveData(String name,String value) {
        SharedPreferences.Editor editor = getSharedPreferences("Oauth", Context.MODE_PRIVATE).edit();
        editor.putString(name,value);
        Log.d(TAG,"AcessToken" + value);
        editor.commit();


    }
}
