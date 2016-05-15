package com.example.qkx.multiweibo.previous;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 2015/11/12.
 */
public class WeiboDisplayActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "WeiboDisplayActivity";

    private LinearLayout display;
    private ListView listView;
    private MyAdapter adapter;

    private Button refresh;//刷新按钮
    private Button refreshMore;//加载按钮
    private ProgressDialog progressDialog;//等待进度

    private List<WeiboDetail.Statuse> list = new ArrayList<>();
    private WeiboDetail weiboDetail[] = {};

    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    private String access_token;

    private int refreshPosition = 0;//记录刷新的位置
    private int i = 0;//请求次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_list_layout);

        showProgressDialog();
        init();
    }

    private void init() {

        display = (LinearLayout) findViewById(R.id.display);
//        display.setVisibility(View.INVISIBLE);

        refreshMore = (Button) findViewById(R.id.refreshMore);
        refreshMore.setVisibility(View.GONE);
        refreshMore.setOnClickListener(this);

        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        adapter = new MyAdapter();
        listView = (ListView) findViewById(R.id.weibo_list);
        listView.setAdapter(adapter);

        mQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(mQueue, new MyBitmapCache());
        /*imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String s) {
                return null;
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {
            }
        });*/

        //获取AcessToken
        SharedPreferences spf = getSharedPreferences("Oauth", Context.MODE_PRIVATE);
        access_token = spf.getString("access_token", null);
        Log.d(TAG, "access_token ----->" + access_token);
        queryHome();

    }

    /**
     * 获取主页信息
     */
    private void queryHome() {
        i = (i > 4) ? 4 : i;
        String queryHomeUrl = Constans.HOME_URL + "?access_token=" + access_token + "&count=" + 20 * (i + 1);
        Log.d(TAG, "sendHomeRequst ----->" + queryHomeUrl);
        /**
         * 用Volley发送请求
         */
        StringRequest stringRequest = new StringRequest(queryHomeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        i++;
                        saveData("response", s);
                        Log.d(TAG, "response ----->" + s);
                        handleHomeRequest(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        SharedPreferences spf = getSharedPreferences("Weibo", Context.MODE_PRIVATE);
                        String response = spf.getString("response", null);
                        if (response != null) {
                            handleHomeRequest(response);
                        }
                        Toast.makeText(WeiboDisplayActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error ----->" + volleyError.getMessage());
                    }
                });
        mQueue.add(stringRequest);//开始发送请求
        /**
         * 用HttpUtil发送请求
         */
        /*HttpUtil.sendHttpClientGET(queryHomeUrl, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("WeiboDisplayActivity", "response -----> " + response);
                handleHomeRequest(response);
            }

            @Override
            public void onError(Exception e) {
                Log.d("WeiboDisplayActivity", "query failed");
            }
        });*/
    }

    private void saveData(String name, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("Weibo", Context.MODE_PRIVATE).edit();
        editor.putString(name, value);
        Log.d(TAG, "saved response ----->" + value);
        editor.commit();
    }

    /**
     * 处理返回JSON数据
     * 将其解析为WeiboDetail对象
     * 暂时只解析出TEXT内容
     *
     * @param response
     */
    private void handleHomeRequest(String response) {
        Gson gson = new Gson();
        WeiboDetail weiboDetail2 = gson.fromJson(response, WeiboDetail.class);
        list = weiboDetail2.statuses;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                closeProgressDialog();
                Log.d(TAG, "ListView has notified");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshMore:
                queryHome();
                v.setVisibility(View.GONE);
                break;
            case R.id.refresh:
                i--;
                showProgressDialog();
                queryHome();
                listView.setSelection(0);
            default:
                break;
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 定义ListView适配器
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.weibo_detail_layout, null);

                viewHolder.retweetedText = (TextView) convertView.findViewById(R.id.tv_retweeted_text);

                viewHolder.imageGroup = (RelativeLayout) convertView.findViewById(R.id.imageGroup);
                viewHolder.imgHeadPic = (ImageView) convertView.findViewById(R.id.iv_head_pic);
                viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
                viewHolder.tvWeiboText = (TextView) convertView.findViewById(R.id.tv_status_text);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            /**
             *暂时显示微博文本内容
             */
            viewHolder.tvWeiboText.setText(list.get(position).text);
            viewHolder.tvUserName.setText(list.get(position).user.screen_name);
            /**
             * volley加载显示图片
             */
            ImageLoader.ImageListener headListener = ImageLoader.getImageListener(viewHolder.imgHeadPic, R.drawable.ic_launcher, R.drawable.ic_launcher);
            imageLoader.get(list.get(position).user.profile_image_url, headListener);
            /**
             * 在imageGroup中
             * 加载微博图片内容
             */
            List<WeiboDetail.Statuse.PicUrl> picUrls = list.get(position).pic_urls;
            if (picUrls != null) {
                viewHolder.imageGroup.removeAllViews();
                for (int i = 0; i < picUrls.size(); i++) {
                    ImageView imageView = new ImageView(WeiboDisplayActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(250,250);
                    int side = listView.getWidth() / 3;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(side - 5, side - 5);
//                    params.lay
                    params.topMargin = (i / 3) * side;
//                    params.topMargin = ( i / 3 ) * 260;
                    params.leftMargin = (i % 3) * side;
//                    params.leftMargin = ( i % 3 ) * 260;
                    imageView.setLayoutParams(params);
//                    viewHolder.imageGroup.addView(imageView, 250, 250);
                    viewHolder.imageGroup.addView(imageView);

                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
                    imageLoader.get(picUrls.get(i).thumbnail_pic, listener);
//                    imageLoader.get(picUrls.get(i).thumbnail_pic, listener,imageView.getWidth(),imageView.getHeight());
                }
            }
            /**
             * 显示转发的微博
             */
            viewHolder.retweetedText.setVisibility(View.GONE);
            if (list.get(position).retweeted_status != null) {
                String content = list.get(position).retweeted_status.user.screen_name + "：" + list.get(position).retweeted_status.text;
                viewHolder.retweetedText.setText(content);
                viewHolder.retweetedText.setVisibility(View.VISIBLE);
            }
            /**
             * 显示加载
             */
            if (position == list.size() - 1) {
                refreshMore.setVisibility(View.VISIBLE);
            } else {
                refreshMore.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public ImageView imgHeadPic;
        public TextView tvUserName;
        public TextView tvWeiboText;
        public RelativeLayout imageGroup;

        public TextView retweetedText;
    }
}
