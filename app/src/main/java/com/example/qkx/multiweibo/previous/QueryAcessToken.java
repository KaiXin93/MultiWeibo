package com.example.qkx.multiweibo.previous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.db.WeiboDB;
import com.example.qkx.multiweibo.model.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qkx on 2015/11/1.
 */
public class QueryAcessToken extends Activity {
    private static final String TAG = "QueryAcessToken";
    RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = Volley.newRequestQueue(this);
        getAcessToken();
    }
    private void getAcessToken() {
        final String code = getCode();
        String acess_token_url = Constans.ACESS_TOKEN_URL;

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "error is " + volleyError.getMessage());
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, acess_token_url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("client_id", Constans.CLIENT_ID);
                map.put("client_secret", Constans.CLIENT_SECRECT);
                map.put("grant_type", "authorization_code");
                map.put("code", code);
                map.put("redirect_uri",Constans.REDIRECT_URI);
                return map;
            }
        };
        mQueue.add(stringRequest);

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
