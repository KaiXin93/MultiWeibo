package com.example.qkx.multiweibo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.previous.*;
import com.example.qkx.multiweibo.previous.Constans;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 15/11/23.
 */
public class MyFragment extends Fragment {
    private static final String TAG = "MyFragment";

    private MyAdapter adapter;

    private List<WeiboDetail.Statuse> list = new ArrayList<>();
    private WeiboDetail weiboDetail[] = {};

    private String access_token;

    private RequestQueue mQueue;

    private ImageLoader imageLoader;
    public static MyFragment newInstance(String access_token) {
        MyFragment myFragment = new MyFragment();
        myFragment.setAccess_token(access_token);
        return myFragment;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView -----> start");

        View view = inflater.inflate(R.layout.layout_test_fragment, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate -----> start");
        init();
    }

    private void init() {
        mQueue = Volley.newRequestQueue(getActivity());
        imageLoader = new ImageLoader(mQueue, new MyBitmapCache());

        //获取AcessToken
        /*SharedPreferences spf = getActivity().getSharedPreferences("Oauth", Context.MODE_PRIVATE);
        access_token = spf.getString("access_token", null);
        Log.d(TAG, "access_token ----->" + access_token);*/
        queryHome();
    }

    private void queryHome() {
        String queryHomeUrl = Constans.HOME_URL + "?access_token=" + access_token ;
        Log.d(TAG, "sendHomeRequst ----->" + queryHomeUrl);
        /**
         * 用Volley发送请求
         */
        StringRequest stringRequest = new StringRequest(queryHomeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("WeiboDisplayActivity", "response ----->" + s);
                        handleHomeRequest(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "加载失败...", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error ----->" + volleyError.getMessage());
                    }
                });
        mQueue.add(stringRequest);//开始发送请求
    }

    private void handleHomeRequest(String response) {
        Gson gson = new Gson();
        WeiboDetail weiboDetail2 = gson.fromJson(response, WeiboDetail.class);
        list = weiboDetail2.statuses;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                Log.d(TAG, "ListView has notified");
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(),R.layout.listview_test_layout,null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.id_tv_test);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(list.get(position).text);

            return convertView;
        }
    }
    class ViewHolder {
        public TextView text;
    }
}
